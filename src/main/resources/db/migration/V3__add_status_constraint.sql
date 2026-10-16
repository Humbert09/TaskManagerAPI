--Agregamos la restricción a la columna para solo aceptar 3 valores.
ALTER TABLE management.tasks
ADD CONSTRAINT chk_tasks_status
CHECK (status IN ('Pending', 'InProgress', 'Completed'));