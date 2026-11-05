INSERT INTO pacientes (id, nombres, apellidos, documento, fecha_nacimiento, sexo, telefono, email, direccion, tipo_sangre, alergias, created_at) VALUES
(1, 'Ana Maria', 'Torres Rojas', '45872136', '1990-05-14', 'FEMENINO', '987654321', 'ana.torres@example.com', 'Av. Los Alamos 123, Lima', 'O+', 'Penicilina', NOW()),
(2, 'Carlos Alberto', 'Mendoza Ruiz', '70123456', '1985-11-02', 'MASCULINO', '912345678', 'carlos.mendoza@example.com', 'Jr. Las Flores 456, Lima', 'A+', 'Ninguna', NOW()),
(3, 'Lucia Fernanda', 'Ramirez Soto', '76543210', '2001-03-27', 'FEMENINO', '923456789', 'lucia.ramirez@example.com', 'Calle Los Pinos 789, Lima', 'B+', 'Polvo', NOW()),
(4, 'Jorge Luis', 'Paredes Vega', '12345678', '1978-07-09', 'MASCULINO', '934567890', 'jorge.paredes@example.com', 'Av. Central 321, Lima', 'AB+', 'Ninguna', NOW()),
(5, 'Maria Elena', 'Chavez Flores', '40987654', '1995-01-18', 'FEMENINO', '945678901', 'maria.chavez@example.com', 'Jr. San Martin 654, Lima', 'O-', 'Lacteos', NOW()),
(6, 'Diego Alonso', 'Herrera Cruz', '72345678', '2010-09-30', 'MASCULINO', '956789012', 'diego.herrera@example.com', 'Av. Universitaria 987, Lima', 'A-', 'Ninguna', NOW());

INSERT INTO medicos (id, nombres, apellidos, especialidad, colegiatura, telefono, email, horario, activo, created_at) VALUES
(1, 'Patricia', 'Salazar Nunez', 'Medicina General', 'CMP-045678', '998877665', 'patricia.salazar@clinica.com', 'Lunes a viernes 08:00 - 14:00', 1, NOW()),
(2, 'Ricardo', 'Valdivia Ponce', 'Cardiologia', 'CMP-023456', '997766554', 'ricardo.valdivia@clinica.com', 'Lunes, miercoles y viernes 09:00 - 17:00', 1, NOW()),
(3, 'Elena', 'Vargas Medina', 'Pediatria', 'CMP-067890', '996655443', 'elena.vargas@clinica.com', 'Martes a sabado 08:00 - 13:00', 1, NOW()),
(4, 'Miguel Angel', 'Rojas Tello', 'Dermatologia', 'CMP-034567', '995544332', 'miguel.rojas@clinica.com', 'Lunes a jueves 14:00 - 19:00', 1, NOW()),
(5, 'Sofia', 'Campos Herrera', 'Ginecologia', 'CMP-078901', '994433221', 'sofia.campos@clinica.com', 'Martes y jueves 09:00 - 16:00', 1, NOW());

INSERT INTO citas (id, paciente_id, medico_id, fecha_hora, motivo, estado, observaciones, created_at) VALUES
(1, 1, 1, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '10:00:00'), 'Control general y revision de examenes', 'ATENDIDA', 'Paciente refiere cansancio leve.', NOW()),
(2, 2, 2, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '11:30:00'), 'Dolor en el pecho al esfuerzo', 'ATENDIDA', 'Se solicita control de presion arterial.', NOW()),
(3, 3, 3, TIMESTAMP(CURDATE(), '09:00:00'), 'Control pediatrico', 'PROGRAMADA', 'Trae resultados de laboratorio.', NOW()),
(4, 4, 4, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00:00'), 'Revision de lesion en la piel', 'PROGRAMADA', 'Lesion en antebrazo derecho.', NOW()),
(5, 5, 1, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '11:00:00'), 'Chequeo preventivo', 'PROGRAMADA', 'Paciente solicita descarte general.', NOW()),
(6, 6, 3, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 2 DAY), '15:00:00'), 'Control de fiebre y tos', 'PROGRAMADA', 'Trae radiografia de torax.', NOW()),
(7, 1, 2, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 3 DAY), '09:30:00'), 'Evaluacion cardiologica', 'PROGRAMADA', 'Antecedente de hipertension familiar.', NOW()),
(8, 2, 1, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 5 DAY), '16:00:00'), 'Dolor de cabeza recurrente', 'CANCELADA', 'Paciente no pudo asistir.', NOW()),
(9, 3, 1, TIMESTAMP(CURDATE(), '16:00:00'), 'Dolor abdominal', 'PROGRAMADA', 'Acude con su madre.', NOW()),
(10, 6, 3, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 3 DAY), '09:00:00'), 'Fiebre y tos persistente', 'ATENDIDA', 'Se indico tratamiento sintomatico.', NOW()),
(11, 4, 4, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 10 DAY), '10:00:00'), 'Dermatitis por contacto', 'ATENDIDA', 'Mejoria parcial con tratamiento.', NOW()),
(12, 5, 2, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:30:00'), 'Palpitaciones ocasionales', 'PROGRAMADA', 'Trae electrocardiograma previo.', NOW());

INSERT INTO historiales (id, paciente_id, medico_id, cita_id, fecha_consulta, motivo_consulta, diagnostico, tratamiento, notas, peso, talla, presion_arterial, temperatura, frecuencia_cardiaca, created_at) VALUES
(1, 1, 1, 1, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '10:30:00'), 'Control general y revision de examenes', 'Paciente estable. Hemograma y perfil lipidico dentro de parametros normales.', 'Dieta equilibrada, actividad fisica moderada y control en seis meses.', 'Se refuerzan habitos saludables.', 62.50, 1.62, '110/70', 36.5, 72, NOW()),
(2, 2, 2, 2, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '12:00:00'), 'Dolor en el pecho al esfuerzo', 'Angina estable. Se recomienda valoracion cardiologica integral.', 'Reposo relativo, aspirina 100 mg diario y control en 15 dias.', 'Electrocardiograma sin cambios agudos.', 84.00, 1.75, '140/90', 36.7, 78, NOW()),
(3, 6, 3, 10, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 3 DAY), '09:30:00'), 'Fiebre y tos persistente', 'Infeccion respiratoria alta de probable etiologia viral.', 'Amoxicilina pediatrica, ibuprofeno condicional y abundantes liquidos.', 'Control en 48 horas si persiste la fiebre.', 35.20, 1.40, '100/65', 38.2, 96, NOW()),
(4, 4, 4, 11, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 10 DAY), '10:30:00'), 'Dermatitis por contacto', 'Dermatitis de contacto en antebrazo derecho.', 'Crema topica y evitar exposicion al agente irritante.', 'Se indica control en dos semanas.', 78.00, 1.70, '120/80', 36.4, 70, NOW());

INSERT INTO recetas (id, paciente_id, medico_id, historial_id, fecha_emision, indicaciones_generales, estado, created_at) VALUES
(1, 1, 1, 1, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '10:35:00'), 'Mantener una adecuada hidratacion y evitar automedicacion.', 'EMITIDA', NOW()),
(2, 2, 2, 2, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '12:05:00'), 'Acudir a emergencia si el dolor toracico se intensifica.', 'EMITIDA', NOW()),
(3, 6, 3, 3, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 3 DAY), '09:35:00'), 'Completar el tratamiento antibiotico aunque los sintomas mejoren.', 'EMITIDA', NOW()),
(4, 4, 4, 4, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 10 DAY), '10:35:00'), 'Aplicar la crema solo en la zona afectada y evitar el sol directo.', 'EMITIDA', NOW());

INSERT INTO receta_items (id, receta_id, medicamento, presentacion, dosis, frecuencia, duracion, cantidad, indicaciones) VALUES
(1, 1, 'Paracetamol 500 mg', 'Tableta', '1 tableta', 'Cada 8 horas', '5 dias', '15 tabletas', 'Despues de los alimentos'),
(2, 1, 'Omeprazol 20 mg', 'Capsula', '1 capsula', 'Cada 24 horas', '7 dias', '7 capsulas', 'Antes del desayuno'),
(3, 2, 'Aspirina 100 mg', 'Tableta', '1 tableta', 'Cada 24 horas', '30 dias', '30 tabletas', 'Con alimentos'),
(4, 2, 'Atorvastatina 20 mg', 'Tableta', '1 tableta', 'Cada noche', '30 dias', '30 tabletas', 'Control de colesterol'),
(5, 3, 'Amoxicilina 250 mg/5 ml', 'Jarabe', '5 ml', 'Cada 8 horas', '7 dias', '1 frasco', 'Completar el tratamiento'),
(6, 3, 'Ibuprofeno 100 mg/5 ml', 'Suspension', '5 ml', 'Cada 8 horas si hay fiebre', '3 dias', '1 frasco', 'Despues de los alimentos'),
(7, 4, 'Hidrocortisona 1%', 'Crema', 'Capa fina', 'Cada 12 horas', '10 dias', '1 tubo', 'Solo en la zona afectada'),
(8, 4, 'Loratadina 10 mg', 'Tableta', '1 tableta', 'Cada 24 horas', '7 dias', '7 tabletas', 'Preferiblemente por la noche');

ALTER TABLE pacientes AUTO_INCREMENT = 100;
ALTER TABLE medicos AUTO_INCREMENT = 100;
ALTER TABLE citas AUTO_INCREMENT = 100;
ALTER TABLE historiales AUTO_INCREMENT = 100;
ALTER TABLE recetas AUTO_INCREMENT = 100;
ALTER TABLE receta_items AUTO_INCREMENT = 100;
