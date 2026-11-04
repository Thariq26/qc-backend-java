package com.gmp.defect_system.service;

import com.gmp.defect_system.entity.Machine;
import com.gmp.defect_system.repository.MachineRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MachineService {

    private final MachineRepository machineRepository;

    public MachineService(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }
    public List<Machine> getAllMachines() {
        return machineRepository.findAll();
    }
    public Machine createMachine(Machine machine) {
        return machineRepository.save(machine);
    }
    public void deleteMachine(Long id) {
        machineRepository.deleteById(id);
    }
}