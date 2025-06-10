package com.udea.GPX.repository;

import com.udea.GPX.model.Event;
import com.udea.GPX.model.Stage;
import com.udea.GPX.model.StageResult;
import com.udea.GPX.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IStageResultRepository extends JpaRepository<StageResult, Long> {

        List<StageResult> findByVehicleAndStage_Event(Vehicle vehicle, Event event);

        List<StageResult> findByVehicleAndStage(Vehicle vehicle, Stage stage);

        List<StageResult> findByStage(Stage stage);

        // === CONSULTAS OPTIMIZADAS PARA RESOLVER ISSUE-002 ===

        /**
         * Encuentra todos los resultados de un evento específico
         * Reemplaza: findAll().stream().filter(r ->
         * r.getStage().getEvent().getId().equals(eventId))
         */
        @Query("SELECT sr FROM StageResult sr WHERE sr.stage.event.id = :eventId")
        List<StageResult> findByEventId(@Param("eventId") Long eventId);

        /**
         * Encuentra resultados por evento y rango de etapas
         * Reemplaza consulta ineficiente en getResultsByStageRange
         */
        @Query("SELECT sr FROM StageResult sr " +
                        "WHERE sr.stage.event.id = :eventId " +
                        "AND sr.stage.orderNumber BETWEEN :stageStart AND :stageEnd " +
                        "AND sr.stage.isNeutralized = false " +
                        "ORDER BY sr.timestamp")
        List<StageResult> findByEventIdAndStageRange(@Param("eventId") Long eventId,
                        @Param("stageStart") Integer stageStart,
                        @Param("stageEnd") Integer stageEnd);

        /**
         * Encuentra resultados por evento y número de etapa específico
         * Para clasificaciones por etapa
         */
        @Query("SELECT sr FROM StageResult sr " +
                        "WHERE sr.stage.event.id = :eventId " +
                        "AND sr.stage.orderNumber = :stageNumber")
        List<StageResult> findByEventIdAndStageNumber(@Param("eventId") Long eventId,
                        @Param("stageNumber") Integer stageNumber);

        /**
         * Encuentra resultados por evento y categoría
         * Para clasificaciones por categoría
         */
        @Query("SELECT sr FROM StageResult sr " +
                        "WHERE sr.stage.event.id = :eventId " +
                        "AND sr.vehicle.category.id = :categoryId")
        List<StageResult> findByEventIdAndCategoryId(@Param("eventId") Long eventId,
                        @Param("categoryId") Long categoryId);

        /**
         * Encuentra resultados por evento ordenados para clasificación general
         * Reemplaza consulta ineficiente en getClasificacionGeneral
         */
        @Query("SELECT sr FROM StageResult sr " +
                        "WHERE sr.stage.event.id = :eventId " +
                        "ORDER BY sr.stage.orderNumber, sr.timestamp")
        List<StageResult> findByEventIdOrderedForClassification(@Param("eventId") Long eventId);

        /**
         * Encuentra resultados agrupados por vehículo para un evento
         * Optimizado para updateElapsedTimesForEvent
         */
        @Query("SELECT sr FROM StageResult sr " +
                        "WHERE sr.stage.event.id = :eventId " +
                        "AND sr.timestamp IS NOT NULL " +
                        "ORDER BY sr.vehicle.id, sr.stage.orderNumber")
        List<StageResult> findByEventIdWithTimestampOrderedByVehicleAndStage(@Param("eventId") Long eventId);

        /**
         * Cuenta resultados por evento para métricas
         */
        @Query("SELECT COUNT(sr) FROM StageResult sr WHERE sr.stage.event.id = :eventId")
        Long countByEventId(@Param("eventId") Long eventId);

        /**
         * Encuentra resultados con penalizaciones para un evento
         */
        @Query("SELECT sr FROM StageResult sr " +
                        "WHERE sr.stage.event.id = :eventId " +
                        "AND (sr.penaltyWaypoint IS NOT NULL " +
                        "     OR sr.penaltySpeed IS NOT NULL " +
                        "     OR sr.discountClaim IS NOT NULL)")
        List<StageResult> findByEventIdWithPenalties(@Param("eventId") Long eventId);
}
