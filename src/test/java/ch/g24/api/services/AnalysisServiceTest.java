package ch.g24.api.services;

import ch.g24.api.models.*;
import ch.g24.api.repository.entities.DataEntity;
import ch.g24.api.repository.entities.MedicationEntity;
import ch.g24.api.repository.entities.UnitEntity;
import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.DataRepository;
import ch.g24.api.repository.persistence.MedicationRepository;
import ch.g24.api.repository.persistence.UserRepository;
import ch.g24.api.services.AnalysisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalysisServiceTest {

    private DataRepository dataRepository;
    private MedicationRepository medicationRepository;
    private UserRepository userRepository;
    private AnalysisService analysisService;
    private UnitEntity unitEntity;

    @BeforeEach
    void setUp() {
        dataRepository = mock(DataRepository.class);
        medicationRepository = mock(MedicationRepository.class);
        userRepository = mock(UserRepository.class);
        analysisService = new AnalysisService(dataRepository, medicationRepository, userRepository);
        unitEntity = new UnitEntity();
        unitEntity.setUnitId(1L);
        unitEntity.setUnitName("mg/dL");
    }

    @Test
    void testGetDashboard_NoEntries_ReturnsEmptyStructure() {
        long userId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setUnitId("mmol/L");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dataRepository.getDataByUserId(userId)).thenReturn(List.of());

        DashBoardData result = analysisService.getDashboard(userId);

        assertNotNull(result);
        assertNotNull(result.getWeeklyOverview());
        assertEquals(1, result.getWeeklyOverview().getReadings().size());
        assertNull(result.getLatestReadings());  // Because no entries exist
    }

    @Test
    void testGetDashboard_WithEntries_ComputesWeeklyAverage() {
        long userId = 1L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setUnitId("2");

        unitEntity.setUnitId(2L);
        unitEntity.setUnitName("mmol/L");

        DataEntity dataEntity1 = new DataEntity();
        dataEntity1.setMeasurementEntryTime(LocalDateTime.now().minusDays(1));
        dataEntity1.setSugarValue(5.5);
        dataEntity1.setValue("fasting");
        dataEntity1.setUnit(unitEntity);

        DataEntity dataEntity2 = new DataEntity();
        dataEntity2.setMeasurementEntryTime(LocalDateTime.now().minusDays(2));
        dataEntity2.setSugarValue(7.2);
        dataEntity2.setUnit(unitEntity);
        dataEntity2.setValue("post_meal_2hrs_after");

        DataEntity dataEntity3 = new DataEntity();
        dataEntity3.setMeasurementEntryTime(LocalDateTime.now().minusDays(3));
        dataEntity3.setSugarValue(6.9);
        dataEntity3.setUnit(unitEntity);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dataRepository.getDataByUserId(userId)).thenReturn(List.of(dataEntity1, dataEntity2,dataEntity3));

        DashBoardData dashboard = analysisService.getDashboard(userId);

        assertNotNull(dashboard);
        assertNotNull(dashboard.getLatestReadings());
        assertNotNull(dashboard.getWeeklyOverview());
        assertFalse(dashboard.getWeeklyOverview().getReadings().isEmpty());

        // Check calculated weekly average
        double avg = dashboard.getLatestReadings().getWeeklyAverage().getValue();
        assertEquals(6.5, avg);
    }

    @Test
    void testGetDashboard_IncludesMedication() {
        long userId = 2L;

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setUnitId("1");

        DataEntity data = new DataEntity();
        data.setMeasurementEntryTime(LocalDateTime.now().minusDays(3));
        data.setSugarValue(120.0);
        data.setValue("fasting");
        data.setUnit(unitEntity);

        MedicationEntity medicationEntity = new MedicationEntity();
        medicationEntity.setMedicationId(10L);
        medicationEntity.setMedicationName("Tablets");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dataRepository.getDataByUserId(userId)).thenReturn(List.of(data));
        when(medicationRepository.findAllMedicationsByUserId(userId))
                .thenReturn(Optional.of(medicationEntity));

        DashBoardData dashboard = analysisService.getDashboard(userId);

        assertNotNull(dashboard);
        assertEquals(1, dashboard.getMedications().size());
        assertEquals("Tablets", dashboard.getMedications().get(0).getName());
    }

    @Test
    void testCalculateStatus_mmolL() throws Exception {
        var method = AnalysisService.class.getDeclaredMethod("calculateStatus", double.class, String.class);
        method.setAccessible(true);

        assertEquals("low", method.invoke(analysisService, 3.5, "mmol/L"));
        assertEquals("normal", method.invoke(analysisService, 5.5, "mmol/L"));
        assertEquals("elevated", method.invoke(analysisService, 8.5, "mmol/L"));
        assertEquals("high", method.invoke(analysisService, 15.0, "mmol/L"));
    }

    @Test
    void testCalculateStatus_mgdl() throws Exception {
        var method = AnalysisService.class.getDeclaredMethod("calculateStatus", double.class, String.class);
        method.setAccessible(true);

        assertEquals("low", method.invoke(analysisService, 65.0, "mg/dL"));
        assertEquals("normal", method.invoke(analysisService, 100.0, "mg/dL"));
        assertEquals("elevated", method.invoke(analysisService, 150.0, "mg/dL"));
        assertEquals("high", method.invoke(analysisService, 200.0, "mg/dL"));
    }

    @Test
    void testGetDashboard_UserNotFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> analysisService.getDashboard(123));
    }
}

