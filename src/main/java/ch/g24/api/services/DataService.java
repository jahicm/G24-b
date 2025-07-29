package ch.g24.api.services;

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
            dataEntity.setReferenceValue(entry.referenceValue());
            dataEntity.setStatus(entry.status());
            dataRepository.save(dataEntity);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace(); // Use for debugging
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
            data.setReferenceValue(p.getReferenceValue());
            data.setUnit(p.getUnit().getUnitName());
            data.setValue(p.getValue());
            data.setStatus(p.getStatus());
            return data;
        }).toList();
    }
}
