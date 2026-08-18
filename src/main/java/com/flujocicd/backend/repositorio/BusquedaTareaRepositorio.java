package com.flujocicd.backend.repositorio;

import com.flujocicd.backend.modelo.Tarea;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Búsqueda de tareas por texto libre en el título.
 *
 * ATENCIÓN - FALLA INTENCIONAL PARA EL EJERCICIO DE CI/CD:
 * El método buscarPorTitulo concatena directamente el parámetro del usuario
 * dentro de la consulta SQL nativa, en vez de usar un parámetro ligado
 * (bind parameter). Esto es una vulnerabilidad de inyección SQL (CWE-89) y
 * es justo el tipo de hallazgo que SonarCloud/SonarQube y CodeQL deberían
 * marcar como "Blocker" / vulnerabilidad de seguridad en el pipeline.
 *
 * La corrección (a modo de ejercicio) es reemplazar la concatenación por
 * una consulta parametrizada, por ejemplo:
 *
 *   Query query = entityManager.createNativeQuery(
 *       "SELECT * FROM tareas WHERE titulo LIKE CONCAT('%', :texto, '%')", Tarea.class);
 *   query.setParameter("texto", texto);
 */
@Repository
public class BusquedaTareaRepositorio {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Tarea> buscarPorTitulo(String texto) {
        // Vulnerable a propósito (fines de ejercicio): no usar en producción.
        String sql = "SELECT * FROM tareas WHERE titulo LIKE '%" + texto + "%'";
        Query query = entityManager.createNativeQuery(sql, Tarea.class);
        return query.getResultList();
    }
}
