package com.gmp.defect_system.config;

import com.gmp.defect_system.entity.*;
import com.gmp.defect_system.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PartRepository partRepository;
    private final MachineRepository machineRepository;
    private final DefectTypeRepository defectTypeRepository;
    private final ProductionRepository productionRepository;
    private final NgDataRepository ngDataRepository;
    private final CustomerComplaintRepository complaintRepository;

    public DataInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            PartRepository partRepository,
            MachineRepository machineRepository,
            DefectTypeRepository defectTypeRepository,
            ProductionRepository productionRepository,
            NgDataRepository ngDataRepository,
            CustomerComplaintRepository complaintRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.partRepository = partRepository;
        this.machineRepository = machineRepository;
        this.defectTypeRepository = defectTypeRepository;
        this.productionRepository = productionRepository;
        this.ngDataRepository = ngDataRepository;
        this.complaintRepository = complaintRepository;
    }

    @Override
    public void run(String... args) {
        try { seedUsers(); } catch (Exception e) { System.err.println("Seed users notice: " + e.getMessage()); }
        try { seedMachines(); } catch (Exception e) { System.err.println("Seed machines notice: " + e.getMessage()); }
        try { seedDefectTypes(); } catch (Exception e) { System.err.println("Seed defect types notice: " + e.getMessage()); }
        try { seedParts(); } catch (Exception e) { System.err.println("Seed parts notice: " + e.getMessage()); }
        try { cleanupDummyData(); } catch (Exception e) { System.err.println("Cleanup dummy notice: " + e.getMessage()); }
    }

    private void seedUsers() {
        if (!userRepository.existsByUsername("budi_qc")) {
            User budi = new User();
            budi.setUsername("budi_qc");
            budi.setPassword(passwordEncoder.encode("password123"));
            budi.setFullName("Budi Santoso");
            budi.setEmail("budi_qc@galaxy-mandiri.co.id");
            budi.setRole("STAFF_QC");
            userRepository.save(budi);
        } else {
            userRepository.findByUsername("budi_qc").ifPresent(u -> {
                if ("OPERATOR".equalsIgnoreCase(u.getRole())) {
                    u.setRole("STAFF_QC");
                    userRepository.save(u);
                }
            });
        }

        if (!userRepository.existsByUsername("operator")) {
            User op = new User();
            op.setUsername("operator");
            op.setPassword(passwordEncoder.encode("operator123"));
            op.setFullName("Operator Lantai Produksi");
            op.setEmail("operator@galaxy-mandiri.co.id");
            op.setRole("OPERATOR_PRODUKSI");
            userRepository.save(op);
        } else {
            userRepository.findByUsername("operator").ifPresent(u -> {
                if ("OPERATOR".equalsIgnoreCase(u.getRole())) {
                    u.setRole("OPERATOR_PRODUKSI");
                    userRepository.save(u);
                }
            });
        }

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Admin QC GMP");
            admin.setEmail("admin@galaxy-mandiri.co.id");
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
    }

    private void seedMachines() {
        if (machineRepository.count() > 0) return;

        String[][] machines = {
            {"Amada Press 01", "Stamping"},
            {"Amada Press 02", "Stamping"},
            {"Amada Press 03", "Stamping"},
            {"Komatsu Press 01", "Stamping"},
            {"Komatsu Press 02", "Stamping"},
            {"Komatsu Press 03", "Stamping"},
            {"Aida Press 01", "Stamping"},
            {"Aida Press 02", "Stamping"},
            {"Seyi Press 01", "Stamping"},
            {"Seyi Press 02", "Stamping"},
            {"Chin Fong Press 01", "Stamping"},
            {"Chin Fong Press 02", "Stamping"},
            {"Chin Fong Press 03", "Stamping"},
            {"Amada Press Brake RG-50", "Bending"},
            {"Amada Press Brake HDS-8025", "Bending"},
            {"Komatsu Press Brake PHS-60", "Bending"},
            {"Toyokoki Electric Brake APB-2813", "Bending"},
            {"Panasonic Robot Station A", "Welding"},
            {"Panasonic Robot Station B", "Welding"},
            {"Yaskawa Motoman Station A", "Welding"},
            {"Yaskawa Motoman Station B", "Welding"},
            {"OTC Daihen Robot Station A", "Welding"},
            {"OTC Daihen Robot Station B", "Welding"},
            {"Obara Spot Welding 01", "Welding"},
            {"Obara Spot Welding 02", "Welding"},
            {"Dengensha Stationary Spot 01", "Welding"},
            {"Bystronic Fiber Laser 3015", "Finishing"},
            {"Daito Band Saw Station", "Finishing"},
            {"Shuzui Tumbling Deburring Machine", "Finishing"}
        };

        for (String[] m : machines) {
            try {
                Machine machine = new Machine();
                machine.setName(m[0]);
                machine.setLine(m[1]);
                machineRepository.save(machine);
            } catch (Exception ignored) {}
        }
    }

    private void seedDefectTypes() {
        if (defectTypeRepository.count() > 0) return;

        String[][] defects = {
            {"Gompal", "Blanking", "Critical"},
            {"Burry", "Blanking", "High"},
            {"Dent", "Stamping", "Medium"},
            {"Baret", "Stamping", "Low"},
            {"Karat", "Material Stamping", "Medium"},
            {"Hole Miring", "Piercing", "High"},
            {"Pitch Hole Geser", "Piercing", "High"},
            {"Deformasi Lubang", "Piercing", "Medium"},
            {"Double Punch", "Piercing", "Critical"},
            {"Setting Dimensi", "Drawing", "High"},
            {"Tidak Flat", "Bending", "High"},
            {"Tinggi Sebelah", "Bending", "Medium"},
            {"Variasi Sudut", "Bending", "Medium"},
            {"Visual Bending Crack", "Bending", "Critical"},
            {"Tidak Masuk JIG", "Bending", "High"},
            {"Penyok Bending", "Bending", "Medium"},
            {"Undercut Bending", "Bending", "High"},
            {"Flange Keriting", "Drawing", "Medium"},
            {"Burr Bending", "Bending", "Low"},
            {"Jumping Proses", "Quality Check", "Critical"},
            {"Overheat Spot", "Welding Spot", "High"},
            {"Underheat Spot", "Welding Spot", "Critical"},
            {"Keropos", "Welding Robot", "Critical"},
            {"Nut Miring", "Welding Projection", "High"},
            {"Salah Nut", "Welding Projection", "Critical"},
            {"Nut Lepas", "Welding Projection", "Critical"},
            {"Spatter Berlebih", "Welding Robot", "Low"},
            {"Penetrasi Kurang", "Welding Robot", "Critical"},
            {"NG Setting", "Welding JIG", "High"},
            {"Retak Tarik", "Drawing", "Critical"},
            {"Gelombang", "Stamping", "Medium"},
            {"Kontaminasi Minyak", "Stamping", "Low"},
            {"Miss Alignment", "Assembly", "High"}
        };

        for (String[] d : defects) {
            DefectType dt = new DefectType();
            dt.setName(d[0]);
            dt.setProses(d[1]);
            dt.setCategory(d[2]);
            dt.setDescription("Defect " + d[0] + " pada proses " + d[1]);
            defectTypeRepository.save(dt);
        }
    }

    private void seedParts() {
        if (partRepository.count() > 0) return;

        String[][] parts = {
            {"PT-1001", "Front Bracket A"}, {"PT-1002", "Rear Flange B"}, {"PT-1003", "Side Reinforce L"},
            {"PT-1004", "Side Reinforce R"}, {"PT-1005", "Exhaust Hanger C"}, {"PT-1006", "Main Stay Plate Upper"},
            {"PT-1007", "Main Stay Plate Lower"}, {"PT-1008", "Battery Tray Bracket"}, {"PT-1009", "Seat Lock Bracket Inner"},
            {"PT-1010", "Seat Lock Bracket Outer"}, {"PT-1011", "Footrest Hanger Left"}, {"PT-1012", "Footrest Hanger Right"},
            {"PT-1013", "Muffler Stay Bracket"}, {"PT-1014", "Engine Mount Bracket L"}, {"PT-1015", "Engine Mount Bracket R"},
            {"PT-1016", "Fender Support Plate"}, {"PT-1017", "Center Stand Stopper"}, {"PT-1018", "Side Stand Pivot Base"},
            {"PT-1019", "Fuel Tank Stay Front"}, {"PT-1020", "Fuel Tank Stay Rear"}, {"PT-1021", "Air Cleaner Bracket"},
            {"PT-1022", "Rear Grip Base Plate L"}, {"PT-1023", "Rear Grip Base Plate R"}, {"PT-1024", "Radiator Stay Plate Lower"},
            {"PT-1025", "Ignition Coil Bracket"}, {"PT-1026", "Horn Mounting Stay"}, {"PT-1027", "Speedometer Stay Plate"},
            {"PT-1028", "Headlight Support Frame"}, {"PT-1029", "Taillight Base Mount"}, {"PT-1030", "License Plate Stay Rear"},
            {"PT-2001", "Seat Cross Member Front"}, {"PT-2002", "Seat Cross Member Rear"}, {"PT-2003", "Floor Gusset Reinforce L"},
            {"PT-2004", "Floor Gusset Reinforce R"}, {"PT-2005", "Center Pillar Inner Lower L"}, {"PT-2006", "Center Pillar Inner Lower R"},
            {"PT-2007", "Front Bumper Reinforce Bar"}, {"PT-2008", "Rear Bumper Corner Stay L"}, {"PT-2009", "Rear Bumper Corner Stay R"},
            {"PT-2010", "Cowl Top Plate Inner"}, {"PT-2011", "Radiator Support Upper Cross"}, {"PT-2012", "Radiator Support Lower Stay"},
            {"PT-2013", "Hood Lock Stay Center"}, {"PT-2014", "Subframe Stiffener Plate L"}, {"PT-2015", "Subframe Stiffener Plate R"},
            {"PT-2016", "Door Hinge Backing Plate Upper"}, {"PT-2017", "Door Hinge Backing Plate Lower"}, {"PT-2018", "Side Member Front Cap L"},
            {"PT-2019", "Side Member Front Cap R"}, {"PT-2020", "Suspension Tower Gusset L"}, {"PT-2021", "Suspension Tower Gusset R"},
            {"PT-2022", "Brake Pedal Support Bracket"}, {"PT-2023", "Steering Column Base Stay"}, {"PT-2024", "Transmission Mount Bracket"},
            {"PT-2025", "Exhaust Heat Deflector L"}, {"PT-3001", "Front Floor Cross Member"}, {"PT-3002", "Center Floor Cross Member"},
            {"PT-3003", "Rear Floor Side Member L"}, {"PT-3004", "Rear Floor Side Member R"}, {"PT-3005", "Sill Side Outer Extension L"},
            {"PT-3006", "Sill Side Outer Extension R"}, {"PT-3007", "Front Fender Bracket L"}, {"PT-3008", "Front Fender Bracket R"},
            {"PT-3009", "Rear Wheelhouse Inner Plate L"}, {"PT-3010", "Rear Wheelhouse Inner Plate R"}, {"PT-3011", "Battery Ground Stay Plate"},
            {"PT-3012", "ECU Mounting Bracket"}, {"PT-3013", "Front Seat Track Bracket Inner"}, {"PT-3014", "Front Seat Track Bracket Outer"},
            {"PT-3015", "Rear Seat Lock Retainer"}, {"PT-3016", "Fuel Filler Neck Shield"}, {"PT-3017", "Spare Tire Carrier Plate"},
            {"PT-3018", "Muffler Protector Bracket"}, {"PT-3019", "Tow Hook Reinforce Plate"}, {"PT-3020", "Quarter Inner Bracket L"},
            {"PT-3021", "Quarter Inner Bracket R"}, {"PT-3022", "Back Door Lock Reinforce"}, {"PT-3023", "Roof Side Rail Gusset L"},
            {"PT-3024", "Roof Side Rail Gusset R"}, {"PT-3025", "Jack Support Under Bracket"}, {"PT-4001", "Main Stay Plate Front"},
            {"PT-4002", "Main Stay Plate Rear"}, {"PT-4003", "Radiator Cross Member Lower"}, {"PT-4004", "Radiator Side Stiffener L"},
            {"PT-4005", "Radiator Side Stiffener R"}, {"PT-4006", "Stabilizer Bar Bracket Front"}, {"PT-4007", "Front Wheel Apron Gusset L"},
            {"PT-4008", "Front Wheel Apron Gusset R"}, {"PT-4009", "Floor Side Member Inner L"}, {"PT-4010", "Floor Side Member Inner R"},
            {"PT-4011", "Rear Spring Bumper Bracket"}, {"PT-4012", "Rear Shock Tower Plate L"}, {"PT-4013", "Rear Shock Tower Plate R"},
            {"PT-4014", "Silencer Mounting Bracket"}, {"PT-4015", "Wiper Motor Mount Base"}, {"PT-4016", "Instrument Panel Support L"},
            {"PT-4017", "Instrument Panel Support R"}, {"PT-4018", "Center Console Fix Bracket"}, {"PT-4019", "Glove Box Hinge Plate"},
            {"PT-4020", "Rear End Cross Plate Lower"}, {"PT-5001", "Front Side Member Extension L"}, {"PT-5002", "Front Side Member Extension R"},
            {"PT-5003", "Subframe Front Plate Center"}, {"PT-5004", "Bumper Impact Box Upper L"}, {"PT-5005", "Bumper Impact Box Upper R"},
            {"PT-5006", "Side Sill Outer Reinforce L"}, {"PT-5007", "Side Sill Outer Reinforce R"}, {"PT-5008", "Fuel Tank Heat Shield Upper"},
            {"PT-5009", "Rear Floor Cross Stiffener"}, {"PT-5010", "Tailgate Lock Retainer Plate"}, {"PT-5011", "Spare Wheel Anchor Bracket"},
            {"PT-6001", "Cowl Cross Member Outer L"}, {"PT-6002", "Cowl Cross Member Outer R"}, {"PT-6003", "Battery Base Frame Member"},
            {"PT-6004", "Inverter Mounting Bracket"}, {"PT-6005", "Floor Tunnel Side Rail L"}, {"PT-6006", "Floor Tunnel Side Rail R"},
            {"PT-6007", "Rear Floor Side Bracket L"}, {"PT-6008", "Rear Floor Side Bracket R"}, {"PT-6009", "High Voltage Cable Guard"},
            {"PT-6010", "Underbody Aerodynamic Shield Plate"}
        };

        for (String[] p : parts) {
            if (!partRepository.existsByPartNumber(p[0])) {
                Part part = new Part();
                part.setPartNumber(p[0]);
                part.setPartName(p[1]);
                part.setStatus("Active");
                partRepository.save(part);
            }
        }
    }

    private void cleanupDummyData() {
        try {
            complaintRepository.findAll().stream()
                .filter(c -> "LOT-202610-09A".equalsIgnoreCase(c.getLotNumber()) || "PT Astra Honda Motor".equalsIgnoreCase(c.getCustomerName()))
                .forEach(complaintRepository::delete);
        } catch (Exception e) {
            System.err.println("Cleanup dummy complaints notice: " + e.getMessage());
        }

        try {
            ngDataRepository.findAll().stream()
                .filter(ng -> "budi_qc".equalsIgnoreCase(ng.getCreatedBy()) && ng.getRemarks() != null && ng.getRemarks().contains("clearance aus"))
                .forEach(ngDataRepository::delete);
        } catch (Exception e) {
            System.err.println("Cleanup dummy NG notice: " + e.getMessage());
        }

        try {
            productionRepository.findAll().stream()
                .filter(p -> "budi_qc".equalsIgnoreCase(p.getCreatedBy()) && 
                    (("Amada Press 01".equalsIgnoreCase(p.getMachineName()) && p.getTargetQuantity() == 4000) ||
                     ("Panasonic Robot Station A".equalsIgnoreCase(p.getMachineName()) && p.getTargetQuantity() == 3000)))
                .forEach(productionRepository::delete);
        } catch (Exception e) {
            System.err.println("Cleanup dummy production notice: " + e.getMessage());
        }
    }
}
