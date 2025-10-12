package ch.g24.api.services;

import ch.g24.api.enums.Status;
import ch.g24.api.models.Entry;
import ch.g24.api.repository.entities.DataEntity;
import ch.g24.api.repository.entities.UnitEntity;
import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.DataRepository;
import ch.g24.api.repository.persistence.UnitRepository;
import ch.g24.api.repository.persistence.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataServiceTest {

    @Mock
    private DataRepository dataRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private DataService dataService;


    @Test
    void addEntry_shouldSaveDataEntity_whenUserAndUnitExist() {
        // given
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1L);

        UnitEntity unitEntity = new UnitEntity();
        unitEntity.setUnitId(2L);
        unitEntity.setUnitName("mmol/L");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(unitRepository.findById(2L)).thenReturn(Optional.of(unitEntity));

        Entry entry = new Entry(
                "1",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "fasting",
                5.5,
                "3.9-5.6",
                "2",
                ""
        );

        // when
        boolean result = dataService.addEntry(entry);

        // then
        assertTrue(result);
        verify(dataRepository, times(1)).save(any(DataEntity.class));
    }

    @Test
    void addEntry_shouldReturnFalse_whenUserNotFound() {

        Entry entry = new Entry("299L", LocalDateTime.now(), LocalDateTime.now(), "fasting", 5.0, "3.9-5.6", "2", "");
        boolean result = dataService.addEntry(entry);
        assertFalse(result);
    }

    @Test
    void addEntry_shouldSetCorrectStatusForFastingNormal() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(1L);
        UnitEntity unitEntity = new UnitEntity();
        unitEntity.setUnitId(2L);
        unitEntity.setUnitName("mmol/L");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(unitRepository.findById(2L)).thenReturn(Optional.of(unitEntity));

        Entry entry = new Entry(
                "1",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "fasting",
                5.5,
                "3.9-5.6",
                "2",
                ""
        );

        ArgumentCaptor<DataEntity> captor = ArgumentCaptor.forClass(DataEntity.class);

        boolean result = dataService.addEntry(entry);

        assertTrue(result);
        verify(dataRepository).save(captor.capture());
        DataEntity savedEntity = captor.getValue();

        assertEquals(Status.NORMAL.getStatus(), savedEntity.getStatus());
        assertEquals("3.9-5.6", savedEntity.getReferenceValue());
    }
}
