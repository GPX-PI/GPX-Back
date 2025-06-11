package com.udea.gpx.service;

import com.udea.gpx.util.TestDataBuilder;
import com.udea.gpx.model.Category;
import com.udea.gpx.model.Event;
import com.udea.gpx.model.EventCategory;
import com.udea.gpx.repository.IEventCategoryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("EventCategoryService Tests")
class EventCategoryServiceTest {

    @Mock
    private IEventCategoryRepository eventCategoryRepository;

    @InjectMocks
    private EventCategoryService eventCategoryService;

    private EventCategory testEventCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Event testEvent = TestDataBuilder.buildEvent(1L, "Test Event");
        Category testCategory = TestDataBuilder.buildCategory(1L, "Test Category");
        testEventCategory = TestDataBuilder.buildEventCategory(1L, testEvent, testCategory);
    }

    // ========== GET ALL TESTS ==========
    @Test
    @DisplayName("getAll - Debe retornar todas las categorías de evento")
    void getAll_shouldReturnAllEventCategories() {
        // Given
        Event testEvent2 = TestDataBuilder.buildEvent(2L, "Test Event 2");
        Category testCategory2 = TestDataBuilder.buildCategory(2L, "Test Category 2");
        EventCategory eventCategory2 = TestDataBuilder.buildEventCategory(2L, testEvent2, testCategory2);
        List<EventCategory> eventCategories = Arrays.asList(testEventCategory, eventCategory2);
        when(eventCategoryRepository.findAll()).thenReturn(eventCategories);

        // When
        List<EventCategory> result = eventCategoryService.getAll();

        // Then
        assertThat(result)
                .hasSize(2)
                .containsExactly(testEventCategory, eventCategory2);
        verify(eventCategoryRepository).findAll();
    }

    @Test
    @DisplayName("getAll - Debe retornar lista vacía cuando no hay categorías")
    void getAll_shouldReturnEmptyListWhenNoCategories() {
        // Given
        when(eventCategoryRepository.findAll()).thenReturn(List.of());

        // When
        List<EventCategory> result = eventCategoryService.getAll();

        // Then
        assertThat(result).isEmpty();
        verify(eventCategoryRepository).findAll();
    }

    // ========== SAVE TESTS ==========
    @Test
    @DisplayName("save - Debe guardar nueva categoría de evento exitosamente")
    void save_shouldSaveNewEventCategorySuccessfully() {
        // Given
        Event newEvent = TestDataBuilder.buildEvent(3L, "New Event");
        Category newCategory = TestDataBuilder.buildCategory(3L, "New Category");
        EventCategory newEventCategory = TestDataBuilder.buildEventCategory(null, newEvent, newCategory);
        EventCategory savedEventCategory = TestDataBuilder.buildEventCategory(3L, newEvent, newCategory);

        when(eventCategoryRepository.save(newEventCategory)).thenReturn(savedEventCategory);

        // When
        EventCategory result = eventCategoryService.save(newEventCategory);

        // Then
        assertThat(result).isEqualTo(savedEventCategory);
        assertThat(result.getId()).isEqualTo(3L);
        verify(eventCategoryRepository).save(newEventCategory);
    }

    @Test
    @DisplayName("save - Debe actualizar categoría de evento existente")
    void save_shouldUpdateExistingEventCategory() {
        // Given
        Event updatedEvent = TestDataBuilder.buildEvent(2L, "Updated Event");
        testEventCategory.setEvent(updatedEvent);
        when(eventCategoryRepository.save(testEventCategory)).thenReturn(testEventCategory);

        // When
        EventCategory result = eventCategoryService.save(testEventCategory);

        // Then
        assertThat(result).isEqualTo(testEventCategory);
        assertThat(result.getEvent()).isEqualTo(updatedEvent);
        verify(eventCategoryRepository).save(testEventCategory);
    }

    // ========== DELETE TESTS ==========

    @Test
    @DisplayName("delete - Debe eliminar categoría de evento exitosamente")
    void delete_shouldDeleteEventCategorySuccessfully() {
        // Given
        doNothing().when(eventCategoryRepository).deleteById(1L);

        // When
        eventCategoryService.delete(1L);

        // Then
        verify(eventCategoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete - Debe manejar eliminación de categoría inexistente")
    void delete_shouldHandleDeletionOfNonExistentCategory() {
        // Given
        doNothing().when(eventCategoryRepository).deleteById(999L);

        // When
        eventCategoryService.delete(999L);

        // Then
        verify(eventCategoryRepository).deleteById(999L);
    }

    // ========== GET EVENT CATEGORY BY ID TESTS ==========

    @Test
    @DisplayName("getEventCategoryById - Debe retornar categoría existente")
    void getEventCategoryById_shouldReturnExistingCategory() {
        // Given
        when(eventCategoryRepository.findById(1L)).thenReturn(Optional.of(testEventCategory));

        // When
        Object result = eventCategoryService.getEventCategoryById(1L);

        // Then
        assertThat(result).isEqualTo(testEventCategory);
        verify(eventCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("getEventCategoryById - Debe lanzar excepción para categoría inexistente")
    void getEventCategoryById_shouldThrowExceptionForNonExistentCategory() {
        // Given
        when(eventCategoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventCategoryService.getEventCategoryById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("EventCategory not found");

        verify(eventCategoryRepository).findById(999L);
    }

    // ========== GET BY ID TESTS ==========

    @Test
    @DisplayName("getById - Debe retornar categoría existente")
    void getById_shouldReturnExistingCategory() {
        // Given
        when(eventCategoryRepository.findById(1L)).thenReturn(Optional.of(testEventCategory));

        // When
        EventCategory result = eventCategoryService.getById(1L);

        // Then
        assertThat(result).isEqualTo(testEventCategory);
        verify(eventCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("getById - Debe lanzar excepción para categoría inexistente")
    void getById_shouldThrowExceptionForNonExistentCategory() {
        // Given
        when(eventCategoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> eventCategoryService.getById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("EventCategory not found");

        verify(eventCategoryRepository).findById(999L);
    }

    // ========== GET BY EVENT ID TESTS ==========
    @Test
    @DisplayName("getByEventId - Debe retornar categorías del evento")
    void getByEventId_shouldReturnCategoriesForEvent() {
        // Given
        Event testEvent2 = TestDataBuilder.buildEvent(2L, "Test Event 2");
        Category testCategory2 = TestDataBuilder.buildCategory(2L, "Test Category 2");
        EventCategory eventCategory2 = TestDataBuilder.buildEventCategory(2L, testEvent2, testCategory2);
        List<EventCategory> eventCategories = Arrays.asList(testEventCategory, eventCategory2);
        when(eventCategoryRepository.findByEventId(1L)).thenReturn(eventCategories);

        // When
        List<EventCategory> result = eventCategoryService.getByEventId(1L);

        // Then
        assertThat(result)
                .hasSize(2)
                .containsExactly(testEventCategory, eventCategory2);
        verify(eventCategoryRepository).findByEventId(1L);
    }

    @Test
    @DisplayName("getByEventId - Debe retornar lista vacía si evento no tiene categorías")
    void getByEventId_shouldReturnEmptyListIfEventHasNoCategories() {
        // Given
        when(eventCategoryRepository.findByEventId(999L)).thenReturn(List.of());

        // When
        List<EventCategory> result = eventCategoryService.getByEventId(999L);

        // Then
        assertThat(result).isEmpty();
        verify(eventCategoryRepository).findByEventId(999L);
    }

    // ========== EDGE CASES ==========
    @Test
    @DisplayName("save - Debe manejar categoría con campos nulos")
    void save_shouldHandleCategoryWithNullFields() {
        // Given
        EventCategory categoryWithNulls = new EventCategory();
        categoryWithNulls.setEvent(null);
        categoryWithNulls.setCategory(null);

        when(eventCategoryRepository.save(categoryWithNulls)).thenReturn(categoryWithNulls);

        // When
        EventCategory result = eventCategoryService.save(categoryWithNulls);

        // Then
        assertThat(result).isEqualTo(categoryWithNulls);
        assertThat(result.getEvent()).isNull();
        assertThat(result.getCategory()).isNull();
        verify(eventCategoryRepository).save(categoryWithNulls);
    }

    @Test
    @DisplayName("getByEventId - Debe manejar ID de evento nulo graciosamente")
    void getByEventId_shouldHandleNullEventIdGracefully() {
        // Given
        when(eventCategoryRepository.findByEventId(null)).thenReturn(List.of());

        // When
        List<EventCategory> result = eventCategoryService.getByEventId(null);

        // Then
        assertThat(result).isEmpty();
        verify(eventCategoryRepository).findByEventId(null);
    }

    // ========== INTEGRATION TESTS ==========
    @Test
    @DisplayName("Flow completo - Crear, buscar y eliminar categoría de evento")
    void fullFlow_createFindDeleteEventCategory() {
        // Given
        Event flowEvent = TestDataBuilder.buildEvent(5L, "Flow Event");
        Category flowCategory = TestDataBuilder.buildCategory(5L, "Flow Category");
        EventCategory newEventCategory = TestDataBuilder.buildEventCategory(null, flowEvent, flowCategory);
        EventCategory savedEventCategory = TestDataBuilder.buildEventCategory(5L, flowEvent, flowCategory);

        when(eventCategoryRepository.save(newEventCategory)).thenReturn(savedEventCategory);
        when(eventCategoryRepository.findById(5L)).thenReturn(Optional.of(savedEventCategory));
        doNothing().when(eventCategoryRepository).deleteById(5L);

        // When - Create
        EventCategory created = eventCategoryService.save(newEventCategory);

        // Then - Verify creation
        assertThat(created).isEqualTo(savedEventCategory);
        assertThat(created.getId()).isEqualTo(5L);

        // When - Find
        EventCategory found = eventCategoryService.getById(5L);

        // Then - Verify find
        assertThat(found).isEqualTo(savedEventCategory);

        // When - Delete
        eventCategoryService.delete(5L);

        // Then - Verify all operations
        verify(eventCategoryRepository).save(newEventCategory);
        verify(eventCategoryRepository).findById(5L);
        verify(eventCategoryRepository).deleteById(5L);
    }

    @Test
    @DisplayName("Comparación de métodos getEventCategoryById y getById")
    void comparison_getEventCategoryByIdVsGetById() {
        // Given
        when(eventCategoryRepository.findById(1L)).thenReturn(Optional.of(testEventCategory));

        // When
        Object resultAsObject = eventCategoryService.getEventCategoryById(1L);
        EventCategory resultAsEventCategory = eventCategoryService.getById(1L);

        // Then
        assertThat(resultAsObject).isEqualTo(testEventCategory);
        assertThat(resultAsEventCategory).isEqualTo(testEventCategory);
        assertThat(resultAsObject).isEqualTo(resultAsEventCategory);
        verify(eventCategoryRepository, times(2)).findById(1L);
    }
}
