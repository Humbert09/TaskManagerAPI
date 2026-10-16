package yaganaste.taskmanagerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yaganaste.taskmanagerapi.models.Tasks;

public interface TaskRepository extends JpaRepository<Tasks, Integer> {

}
