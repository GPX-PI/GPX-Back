package com.udea.GPX.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ClasificacionCompletaDTO Tests")
class ClasificacionCompletaDTOTest {

    private ClasificacionCompletaDTO clasificacionDTO;
    private List<ClasificacionCompletaDTO.StageTimeCellDTO> stageTimes;

    @BeforeEach
    void setUp() {
        stageTimes = new ArrayList<>();
        stageTimes.add(new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1800, 1L, 30, 15, 10));
        stageTimes.add(new ClasificacionCompletaDTO.StageTimeCellDTO(2, 2100, 2L, 0, 20, 5));

        clasificacionDTO = new ClasificacionCompletaDTO(
                1L, "Toyota Hilux", "Juan Pérez", 1L, "Pro", stageTimes, 3900, "pic.jpg", "Team Alpha");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Constructor should set all values correctly")
        void testConstructor() {
            Long vehicleId = 100L;
            String vehicleName = "Ford Ranger";
            String driverName = "María González";
            Long categoryId = 2L;
            String categoryName = "Amateur";
            List<ClasificacionCompletaDTO.StageTimeCellDTO> times = Arrays.asList(
                    new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1500, 10L, 0, 0, 0));
            Integer totalTime = 1500;
            String userPicture = "https://example.com/maria.jpg";
            String teamName = "Racing Team";

            ClasificacionCompletaDTO dto = new ClasificacionCompletaDTO(
                    vehicleId, vehicleName, driverName, categoryId, categoryName,
                    times, totalTime, userPicture, teamName);

            assertEquals(vehicleId, dto.getVehicleId());
            assertEquals(vehicleName, dto.getVehicleName());
            assertEquals(driverName, dto.getDriverName());
            assertEquals(categoryId, dto.getCategoryId());
            assertEquals(categoryName, dto.getCategoryName());
            assertEquals(times, dto.getStageTimes());
            assertEquals(totalTime, dto.getTotalTime());
            assertEquals(userPicture, dto.getUserPicture());
            assertEquals(teamName, dto.getTeamName());
        }

        @Test
        @DisplayName("Constructor with null optional values should work")
        void testConstructorWithNullOptionalValues() {
            ClasificacionCompletaDTO dto = new ClasificacionCompletaDTO(
                    1L, "Vehicle", "Driver", 1L, "Category",
                    new ArrayList<>(), 0, null, null);

            assertEquals(1L, dto.getVehicleId());
            assertEquals("Vehicle", dto.getVehicleName());
            assertEquals("Driver", dto.getDriverName());
            assertEquals(1L, dto.getCategoryId());
            assertEquals("Category", dto.getCategoryName());
            assertTrue(dto.getStageTimes().isEmpty());
            assertEquals(0, dto.getTotalTime());
            assertNull(dto.getUserPicture());
            assertNull(dto.getTeamName());
        }

        @Test
        @DisplayName("Constructor with empty stage times should work")
        void testConstructorWithEmptyStages() {
            List<ClasificacionCompletaDTO.StageTimeCellDTO> emptyTimes = new ArrayList<>();

            ClasificacionCompletaDTO dto = new ClasificacionCompletaDTO(
                    1L, "Vehicle", "Driver", 1L, "Category",
                    emptyTimes, 0, "pic.jpg", "Team");

            assertNotNull(dto.getStageTimes());
            assertTrue(dto.getStageTimes().isEmpty());
            assertEquals(0, dto.getTotalTime());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("VehicleId getter and setter should work correctly")
        void testVehicleIdGetterSetter() {
            Long vehicleId = 500L;
            clasificacionDTO.setVehicleId(vehicleId);
            assertEquals(vehicleId, clasificacionDTO.getVehicleId());
        }

        @Test
        @DisplayName("VehicleName getter and setter should work correctly")
        void testVehicleNameGetterSetter() {
            String vehicleName = "Chevrolet Colorado";
            clasificacionDTO.setVehicleName(vehicleName);
            assertEquals(vehicleName, clasificacionDTO.getVehicleName());
        }

        @Test
        @DisplayName("DriverName getter and setter should work correctly")
        void testDriverNameGetterSetter() {
            String driverName = "Carlos Mendoza";
            clasificacionDTO.setDriverName(driverName);
            assertEquals(driverName, clasificacionDTO.getDriverName());
        }

        @Test
        @DisplayName("CategoryId getter and setter should work correctly")
        void testCategoryIdGetterSetter() {
            Long categoryId = 3L;
            clasificacionDTO.setCategoryId(categoryId);
            assertEquals(categoryId, clasificacionDTO.getCategoryId());
        }

        @Test
        @DisplayName("CategoryName getter and setter should work correctly")
        void testCategoryNameGetterSetter() {
            String categoryName = "Expert";
            clasificacionDTO.setCategoryName(categoryName);
            assertEquals(categoryName, clasificacionDTO.getCategoryName());
        }

        @Test
        @DisplayName("StageTimes getter and setter should work correctly")
        void testStageTimesGetterSetter() {
            List<ClasificacionCompletaDTO.StageTimeCellDTO> newTimes = Arrays.asList(
                    new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1200, 5L, 10, 5, 0),
                    new ClasificacionCompletaDTO.StageTimeCellDTO(2, 1350, 6L, 20, 0, 15));
            clasificacionDTO.setStageTimes(newTimes);
            assertEquals(newTimes, clasificacionDTO.getStageTimes());
            assertEquals(2, clasificacionDTO.getStageTimes().size());
        }

        @Test
        @DisplayName("TotalTime getter and setter should work correctly")
        void testTotalTimeGetterSetter() {
            Integer totalTime = 5000;
            clasificacionDTO.setTotalTime(totalTime);
            assertEquals(totalTime, clasificacionDTO.getTotalTime());
        }

        @Test
        @DisplayName("UserPicture getter and setter should work correctly")
        void testUserPictureGetterSetter() {
            String userPicture = "https://example.com/new_pic.jpg";
            clasificacionDTO.setUserPicture(userPicture);
            assertEquals(userPicture, clasificacionDTO.getUserPicture());
        }

        @Test
        @DisplayName("TeamName getter and setter should work correctly")
        void testTeamNameGetterSetter() {
            String teamName = "New Racing Team";
            clasificacionDTO.setTeamName(teamName);
            assertEquals(teamName, clasificacionDTO.getTeamName());
        }

        @Test
        @DisplayName("Setting null values should work correctly")
        void testSettingNullValues() {
            clasificacionDTO.setVehicleId(null);
            clasificacionDTO.setVehicleName(null);
            clasificacionDTO.setDriverName(null);
            clasificacionDTO.setCategoryId(null);
            clasificacionDTO.setCategoryName(null);
            clasificacionDTO.setStageTimes(null);
            clasificacionDTO.setTotalTime(null);
            clasificacionDTO.setUserPicture(null);
            clasificacionDTO.setTeamName(null);

            assertNull(clasificacionDTO.getVehicleId());
            assertNull(clasificacionDTO.getVehicleName());
            assertNull(clasificacionDTO.getDriverName());
            assertNull(clasificacionDTO.getCategoryId());
            assertNull(clasificacionDTO.getCategoryName());
            assertNull(clasificacionDTO.getStageTimes());
            assertNull(clasificacionDTO.getTotalTime());
            assertNull(clasificacionDTO.getUserPicture());
            assertNull(clasificacionDTO.getTeamName());
        }
    }

    @Nested
    @DisplayName("StageTimeCellDTO Tests")
    class StageTimeCellDTOTests {

        @Test
        @DisplayName("StageTimeCellDTO constructor should set all values correctly")
        void testStageTimeCellConstructor() {
            Integer stageOrder = 3;
            Integer elapsedTime = 2200;
            Long stageResultId = 15L;
            Integer penaltyWaypoint = 45;
            Integer penaltySpeed = 25;
            Integer discountClaim = 20;

            ClasificacionCompletaDTO.StageTimeCellDTO cell = new ClasificacionCompletaDTO.StageTimeCellDTO(
                    stageOrder, elapsedTime, stageResultId,
                    penaltyWaypoint, penaltySpeed, discountClaim);

            assertEquals(stageOrder, cell.getStageOrder());
            assertEquals(elapsedTime, cell.getElapsedTimeSeconds());
            assertEquals(stageResultId, cell.getStageResultId());
            assertEquals(penaltyWaypoint, cell.getPenaltyWaypointSeconds());
            assertEquals(penaltySpeed, cell.getPenaltySpeedSeconds());
            assertEquals(discountClaim, cell.getDiscountClaimSeconds());
        }

        @Test
        @DisplayName("StageTimeCellDTO getters and setters should work correctly")
        void testStageTimeCellGettersSetters() {
            ClasificacionCompletaDTO.StageTimeCellDTO cell = new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1000, 1L,
                    0, 0, 0);

            // Test setters
            cell.setStageOrder(5);
            cell.setElapsedTimeSeconds(2500);
            cell.setStageResultId(20L);
            cell.setPenaltyWaypointSeconds(60);
            cell.setPenaltySpeedSeconds(30);
            cell.setDiscountClaimSeconds(15);

            // Test getters
            assertEquals(5, cell.getStageOrder());
            assertEquals(2500, cell.getElapsedTimeSeconds());
            assertEquals(20L, cell.getStageResultId());
            assertEquals(60, cell.getPenaltyWaypointSeconds());
            assertEquals(30, cell.getPenaltySpeedSeconds());
            assertEquals(15, cell.getDiscountClaimSeconds());
        }

        @Test
        @DisplayName("getAdjustedTimeSeconds should calculate correctly with all penalties")
        void testAdjustedTimeWithAllPenalties() {
            ClasificacionCompletaDTO.StageTimeCellDTO cell = new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1800, 1L,
                    60, 30, 20);

            // elapsedTime(1800) + penaltyWaypoint(60) + penaltySpeed(30) -
            // discountClaim(20) = 1870
            assertEquals(1870, cell.getAdjustedTimeSeconds());
        }

        @Test
        @DisplayName("getAdjustedTimeSeconds should handle null penalties correctly")
        void testAdjustedTimeWithNullPenalties() {
            ClasificacionCompletaDTO.StageTimeCellDTO cell = new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1500, 1L,
                    null, null, null);

            // elapsedTime(1500) + 0 + 0 - 0 = 1500
            assertEquals(1500, cell.getAdjustedTimeSeconds());
        }

        @Test
        @DisplayName("getAdjustedTimeSeconds should handle mixed null and non-null penalties")
        void testAdjustedTimeWithMixedPenalties() {
            ClasificacionCompletaDTO.StageTimeCellDTO cell = new ClasificacionCompletaDTO.StageTimeCellDTO(1, 2000, 1L,
                    45, null, 10);

            // elapsedTime(2000) + penaltyWaypoint(45) + 0 - discountClaim(10) = 2035
            assertEquals(2035, cell.getAdjustedTimeSeconds());
        }

        @Test
        @DisplayName("getAdjustedTimeSeconds should handle only discount claims")
        void testAdjustedTimeWithOnlyDiscount() {
            ClasificacionCompletaDTO.StageTimeCellDTO cell = new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1800, 1L,
                    null, null, 50);

            // elapsedTime(1800) + 0 + 0 - discountClaim(50) = 1750
            assertEquals(1750, cell.getAdjustedTimeSeconds());
        }

        @Test
        @DisplayName("getAdjustedTimeSeconds should handle only penalties")
        void testAdjustedTimeWithOnlyPenalties() {
            ClasificacionCompletaDTO.StageTimeCellDTO cell = new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1600, 1L,
                    40, 20, null);

            // elapsedTime(1600) + penaltyWaypoint(40) + penaltySpeed(20) - 0 = 1660
            assertEquals(1660, cell.getAdjustedTimeSeconds());
        }

        @Test
        @DisplayName("StageTimeCellDTO should handle zero values correctly")
        void testStageTimeCellWithZeroValues() {
            ClasificacionCompletaDTO.StageTimeCellDTO cell = new ClasificacionCompletaDTO.StageTimeCellDTO(0, 0, 0L, 0,
                    0, 0);

            assertEquals(0, cell.getStageOrder());
            assertEquals(0, cell.getElapsedTimeSeconds());
            assertEquals(0L, cell.getStageResultId());
            assertEquals(0, cell.getPenaltyWaypointSeconds());
            assertEquals(0, cell.getPenaltySpeedSeconds());
            assertEquals(0, cell.getDiscountClaimSeconds());
            assertEquals(0, cell.getAdjustedTimeSeconds());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("DTO with multiple stages should maintain stage order")
        void testMultipleStagesOrder() {
            List<ClasificacionCompletaDTO.StageTimeCellDTO> orderedStages = Arrays.asList(
                    new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1500, 1L, 0, 0, 0),
                    new ClasificacionCompletaDTO.StageTimeCellDTO(2, 1800, 2L, 30, 0, 10),
                    new ClasificacionCompletaDTO.StageTimeCellDTO(3, 1650, 3L, 0, 15, 0),
                    new ClasificacionCompletaDTO.StageTimeCellDTO(4, 1900, 4L, 45, 20, 25));

            ClasificacionCompletaDTO dto = new ClasificacionCompletaDTO(
                    1L, "Multi-Stage Vehicle", "Driver", 1L, "Category",
                    orderedStages, 6900, "pic.jpg", "Team");

            assertEquals(4, dto.getStageTimes().size());
            for (int i = 0; i < orderedStages.size(); i++) {
                assertEquals(i + 1, dto.getStageTimes().get(i).getStageOrder());
            }
        }

        @Test
        @DisplayName("DTO with large time values should work correctly")
        void testLargeTimeValues() {
            ClasificacionCompletaDTO.StageTimeCellDTO largeTime = new ClasificacionCompletaDTO.StageTimeCellDTO(1,
                    999999, 1L, 9999, 5000, 1000);

            ClasificacionCompletaDTO dto = new ClasificacionCompletaDTO(
                    Long.MAX_VALUE, "Large Vehicle", "Driver", Long.MAX_VALUE, "Category",
                    Arrays.asList(largeTime), Integer.MAX_VALUE, "pic.jpg", "Team");

            assertEquals(Long.MAX_VALUE, dto.getVehicleId());
            assertEquals(Integer.MAX_VALUE, dto.getTotalTime());
            assertEquals(999999 + 9999 + 5000 - 1000, largeTime.getAdjustedTimeSeconds());
        }

        @Test
        @DisplayName("DTO representing perfect stage performance should work")
        void testPerfectStagePerformance() {
            ClasificacionCompletaDTO.StageTimeCellDTO perfectStage = new ClasificacionCompletaDTO.StageTimeCellDTO(1,
                    1200, 1L, 0, 0, 0);

            ClasificacionCompletaDTO dto = new ClasificacionCompletaDTO(
                    1L, "Perfect Vehicle", "Perfect Driver", 1L, "Elite",
                    Arrays.asList(perfectStage), 1200, "winner.jpg", "Champions");

            assertEquals(1200, perfectStage.getElapsedTimeSeconds());
            assertEquals(1200, perfectStage.getAdjustedTimeSeconds());
            assertEquals(1200, dto.getTotalTime());
            assertEquals("Champions", dto.getTeamName());
        }

        @Test
        @DisplayName("DTO representing challenging stage with penalties should work")
        void testChallengingStageWithPenalties() {
            ClasificacionCompletaDTO.StageTimeCellDTO challengingStage = new ClasificacionCompletaDTO.StageTimeCellDTO(
                    1, 2400, 1L, 120, 60, 30);

            ClasificacionCompletaDTO dto = new ClasificacionCompletaDTO(
                    1L, "Challenge Vehicle", "Brave Driver", 2L, "Adventure",
                    Arrays.asList(challengingStage), 2550, "adventure.jpg", "Adventurers");

            assertEquals(2400, challengingStage.getElapsedTimeSeconds());
            assertEquals(2550, challengingStage.getAdjustedTimeSeconds()); // 2400 + 120 + 60 - 30
            assertEquals(2550, dto.getTotalTime());
        }

        @Test
        @DisplayName("Multiple DTOs for race classification should work")
        void testMultipleDTOsForRaceClassification() {
            // First place
            ClasificacionCompletaDTO first = new ClasificacionCompletaDTO(
                    1L, "Winner Vehicle", "Speed Racer", 1L, "Pro",
                    Arrays.asList(new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1500, 1L, 0, 0, 0)),
                    1500, "first.jpg", "Speed Team");

            // Second place
            ClasificacionCompletaDTO second = new ClasificacionCompletaDTO(
                    2L, "Runner-up Vehicle", "Fast Driver", 1L, "Pro",
                    Arrays.asList(new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1520, 2L, 10, 0, 0)),
                    1530, "second.jpg", "Fast Team");

            // Third place
            ClasificacionCompletaDTO third = new ClasificacionCompletaDTO(
                    3L, "Bronze Vehicle", "Steady Driver", 1L, "Pro",
                    Arrays.asList(new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1600, 3L, 0, 15, 5)),
                    1610, "third.jpg", "Steady Team");

            // Verify classification order by total time
            assertTrue(first.getTotalTime() < second.getTotalTime());
            assertTrue(second.getTotalTime() < third.getTotalTime());

            assertEquals("Speed Racer", first.getDriverName());
            assertEquals("Fast Driver", second.getDriverName());
            assertEquals("Steady Driver", third.getDriverName());
        }
    }

    @Nested
    @DisplayName("Business Logic Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("DTO should represent complete race stage classification")
        void testCompleteRaceStageClassification() {
            List<ClasificacionCompletaDTO.StageTimeCellDTO> multiStageRace = Arrays.asList(
                    new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1800, 1L, 0, 0, 0), // Perfect stage 1
                    new ClasificacionCompletaDTO.StageTimeCellDTO(2, 2100, 2L, 30, 15, 10), // Stage 2 with penalties
                    new ClasificacionCompletaDTO.StageTimeCellDTO(3, 1950, 3L, 0, 0, 20), // Stage 3 with discount
                    new ClasificacionCompletaDTO.StageTimeCellDTO(4, 2250, 4L, 60, 0, 0) // Stage 4 with waypoint
                                                                                         // penalty
            );

            // Calculate total adjusted time: 1800 + (2100+30+15-10) + (1950-20) + (2250+60)
            // = 1800 + 2135 + 1930 + 2310 = 8175
            ClasificacionCompletaDTO raceClassification = new ClasificacionCompletaDTO(
                    25L, "Rally Beast 4x4", "Roberto Aventurero", 3L, "UTV Expert",
                    multiStageRace, 8175, "https://storage.example.com/roberto.jpg", "Extreme Racers");

            assertEquals(4, raceClassification.getStageTimes().size());
            assertEquals(8175, raceClassification.getTotalTime());
            assertEquals("Roberto Aventurero", raceClassification.getDriverName());
            assertEquals("UTV Expert", raceClassification.getCategoryName());

            // Verify stage calculations
            assertEquals(1800, multiStageRace.get(0).getAdjustedTimeSeconds());
            assertEquals(2135, multiStageRace.get(1).getAdjustedTimeSeconds());
            assertEquals(1930, multiStageRace.get(2).getAdjustedTimeSeconds());
            assertEquals(2310, multiStageRace.get(3).getAdjustedTimeSeconds());
        }

        @Test
        @DisplayName("DTO should maintain data integrity after modifications")
        void testDataIntegrityAfterModifications() {
            ClasificacionCompletaDTO dto = new ClasificacionCompletaDTO(
                    1L, "Original Vehicle", "Original Driver", 1L, "Original Category",
                    new ArrayList<>(), 0, null, null);

            // Initial verification
            assertEquals("Original Vehicle", dto.getVehicleName());
            assertEquals("Original Driver", dto.getDriverName());
            assertTrue(dto.getStageTimes().isEmpty());

            // Add stage times
            List<ClasificacionCompletaDTO.StageTimeCellDTO> newStages = Arrays.asList(
                    new ClasificacionCompletaDTO.StageTimeCellDTO(1, 1500, 10L, 0, 0, 0),
                    new ClasificacionCompletaDTO.StageTimeCellDTO(2, 1650, 11L, 20, 10, 5));
            dto.setStageTimes(newStages);
            dto.setTotalTime(3175); // 1500 + (1650+20+10-5)

            // Update vehicle info
            dto.setVehicleId(100L);
            dto.setVehicleName("Updated Vehicle");
            dto.setDriverName("Updated Driver");
            dto.setCategoryName("Updated Category");
            dto.setTeamName("Updated Team");

            // Final verification
            assertEquals(100L, dto.getVehicleId());
            assertEquals("Updated Vehicle", dto.getVehicleName());
            assertEquals("Updated Driver", dto.getDriverName());
            assertEquals("Updated Category", dto.getCategoryName());
            assertEquals("Updated Team", dto.getTeamName());
            assertEquals(2, dto.getStageTimes().size());
            assertEquals(3175, dto.getTotalTime());
            assertEquals(1675, dto.getStageTimes().get(1).getAdjustedTimeSeconds());
        }

        @Test
        @DisplayName("DTO should handle realistic off-road racing scenarios")
        void testRealisticOffRoadRacingScenarios() {
            // Scenario: Desert rally with navigation challenges and mechanical issues
            List<ClasificacionCompletaDTO.StageTimeCellDTO> desertRally = Arrays.asList(
                    // Stage 1: Clean run
                    new ClasificacionCompletaDTO.StageTimeCellDTO(1, 3600, 101L, 0, 0, 0),
                    // Stage 2: Missed waypoint
                    new ClasificacionCompletaDTO.StageTimeCellDTO(2, 4200, 102L, 300, 0, 0),
                    // Stage 3: Speeding penalty but good claim
                    new ClasificacionCompletaDTO.StageTimeCellDTO(3, 3900, 103L, 0, 180, 60),
                    // Stage 4: Multiple issues
                    new ClasificacionCompletaDTO.StageTimeCellDTO(4, 4800, 104L, 240, 120, 90));

            // Total time calculation:
            // Stage 1: 3600
            // Stage 2: 4200 + 300 = 4500
            // Stage 3: 3900 + 180 - 60 = 4020
            // Stage 4: 4800 + 240 + 120 - 90 = 5070
            // Total: 17190

            ClasificacionCompletaDTO desertRacer = new ClasificacionCompletaDTO(
                    88L, "Polaris RZR Turbo S", "Elena Conquistadora", 4L, "SxS Open",
                    desertRally, 17190, "https://racing.com/elena_helmet.jpg", "Desert Warriors");

            assertEquals(4, desertRacer.getStageTimes().size());
            assertEquals(17190, desertRacer.getTotalTime());
            assertEquals("Elena Conquistadora", desertRacer.getDriverName());
            assertEquals("SxS Open", desertRacer.getCategoryName());
            assertEquals("Desert Warriors", desertRacer.getTeamName());

            // Verify individual stage calculations
            assertEquals(3600, desertRally.get(0).getAdjustedTimeSeconds());
            assertEquals(4500, desertRally.get(1).getAdjustedTimeSeconds());
            assertEquals(4020, desertRally.get(2).getAdjustedTimeSeconds());
            assertEquals(5070, desertRally.get(3).getAdjustedTimeSeconds());

            // Verify penalties were applied correctly
            assertEquals(300, desertRally.get(1).getPenaltyWaypointSeconds());
            assertEquals(180, desertRally.get(2).getPenaltySpeedSeconds());
            assertEquals(60, desertRally.get(2).getDiscountClaimSeconds());
        }
    }
}
