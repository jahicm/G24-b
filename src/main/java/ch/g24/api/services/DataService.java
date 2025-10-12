package ch.g24.api.services;

import ch.g24.api.enums.Status;
import ch.g24.api.enums.SugarUnit;
import ch.g24.api.models.Data;
import ch.g24.api.models.Entry;
import ch.g24.api.repository.entities.DataEntity;
import ch.g24.api.repository.entities.UnitEntity;
import ch.g24.api.repository.entities.UserEntity;
import ch.g24.api.repository.persistence.DataRepository;
import ch.g24.api.repository.persistence.UnitRepository;
import ch.g24.api.repository.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataService {


    private final DataRepository dataRepository;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private static final String VAL_POST_MEAL_PLUS_2 = "post_meal_2hrs_after";
    private static final String VAL_POST_MEAL_PLUS_1 = "post_meal_1hr_after";
    private static final String VAL_FASTING = "fasting";
    private static final String RANDOM = "random";

    @Autowired
    public DataService(DataRepository dataRepository, UserRepository userRepository, UnitRepository unitRepository) {
        this.dataRepository = dataRepository;
        this.userRepository = userRepository;
        this.unitRepository = unitRepository;
    }

    public boolean addEntry(Entry entry) {
        try {
            UserEntity userEntity = userRepository.findById(Long.valueOf(entry.userId()))
                    .orElseThrow(() -> new RuntimeException("User not found: " + entry.userId()));
            UnitEntity unitEntity = unitRepository.findById(Long.valueOf(entry.unit()))
                    .orElseThrow(() -> new RuntimeException("Unit not found: " + entry.unit()));
            DataEntity dataEntity = new DataEntity();
            dataEntity.setUser(userEntity);
            dataEntity.setUnit(unitEntity);
            dataEntity.setDataEntryTime(entry.dataEntryTime());
            dataEntity.setMeasurementEntryTime(entry.measurementTime());
            dataEntity.setValue(entry.value());
            dataEntity.setSugarValue(entry.sugarValue());
            calculateStatus(entry.sugarValue(), entry.unit(), entry.value(), dataEntity);
            dataRepository.save(dataEntity);
            return true;
        } catch (Exception ex) {
            System.err.println("Error adding entry: " + ex.getMessage());
            return false;
        }
    }

    public List<Data> getData(Long userId) {
        List<DataEntity> dataList = dataRepository.getDataByUserId(userId);

        return dataList.stream().map(p -> {
            Data data = new Data();
            data.setDataId(p.getDataId());
            data.setDataEntryTime(p.getDataEntryTime());
            data.setMeasurementTime(p.getMeasurementEntryTime());
            data.setUserId(p.getUser().getUserId());
            data.setSugarValue(p.getSugarValue());
            data.setUnit(p.getUnit().getUnitName());
            data.setValue(p.getValue());
            data.setReferenceValue(p.getReferenceValue());
            data.setStatus(p.getStatus());
            return data;
        }).toList();
    }

    private void calculateStatus(double sugarValue, String sugarUnit, String value, DataEntity dataEntity) {
        // Conversion factor: mg/dL = mmol/L * 18
        boolean isMg = sugarUnit.equals(String.valueOf(SugarUnit.MG_DL.getId()));
        double factor = isMg ? 18.0 : 1.0;

        switch (value) {

            case VAL_POST_MEAL_PLUS_2 -> {

                if (sugarValue < 3.9 * factor) {
                    dataEntity.setReferenceValue(isMg ? "< 70" : "< 3.9");
                    dataEntity.setStatus(Status.LOW.getStatus());
                } else if (sugarValue <= 7.8 * factor) {
                    dataEntity.setReferenceValue(isMg ? "<= 140" : "<= 7.8");
                    dataEntity.setStatus(Status.NORMAL.getStatus());
                } else if (sugarValue <= 11.0 * factor) {
                    dataEntity.setReferenceValue(isMg ? "140-198" : "7.8-11.0");
                    dataEntity.setStatus(Status.ELEVATED.getStatus());
                } else {
                    dataEntity.setReferenceValue(isMg ? "> 198" : "> 11.0");
                    dataEntity.setStatus(Status.HIGH.getStatus());
                }
            }

            case VAL_POST_MEAL_PLUS_1 -> {
                if (sugarValue <= 10.0 * factor) {
                    dataEntity.setReferenceValue(isMg ? "<= 180" : "<= 8.9");
                    dataEntity.setStatus(Status.NORMAL.getStatus());
                } else {
                    dataEntity.setReferenceValue(isMg ? "> 180" : "> 10.0");
                    dataEntity.setStatus(Status.ELEVATED.getStatus());
                }
            }

            case VAL_FASTING -> {
                if (sugarValue >= 3.9 * factor && sugarValue <= 5.6 * factor) {
                    dataEntity.setReferenceValue(isMg ? "70-100" : "3.9-5.6");
                    dataEntity.setStatus(Status.NORMAL.getStatus());
                } else if (sugarValue >= 7.0 * factor) {
                    dataEntity.setReferenceValue(isMg ? ">= 126" : ">= 7.0");
                    dataEntity.setStatus(Status.HIGH.getStatus());
                } else if (sugarValue > 5.6 * factor) {
                    dataEntity.setReferenceValue(isMg ? "> 100" : "> 5.6");
                    dataEntity.setStatus(Status.ELEVATED.getStatus());
                } else if (sugarValue < 3.9 * factor) {
                    dataEntity.setReferenceValue(isMg ? "< 68.5" : "< 3.9");
                    dataEntity.setStatus(Status.LOW.getStatus());
                }
            }

            case RANDOM -> {
                if (sugarValue > 4.0 * factor && sugarValue <= 7.0 * factor) {
                    dataEntity.setReferenceValue(isMg ? "72-126" : "4.0-7.0");
                    dataEntity.setStatus(Status.NORMAL.getStatus());
                } else if (sugarValue <= 10.0 * factor) {
                    dataEntity.setReferenceValue(isMg ? "126-180" : "7.0-10.0");
                    dataEntity.setStatus(Status.ELEVATED.getStatus());
                } else {
                    dataEntity.setReferenceValue(isMg ? "> 180" : "> 10.0");
                    dataEntity.setStatus(Status.HIGH.getStatus());
                }
            }
            default -> {
                dataEntity.setReferenceValue("Invalid value");
                dataEntity.setStatus(Status.UNKNOWN.getStatus());
            }
        }
    }

}
