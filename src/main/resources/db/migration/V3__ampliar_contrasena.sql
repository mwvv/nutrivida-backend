-- Las contraseñas pasan a guardarse hasheadas con BCrypt (60 caracteres),
-- por lo que la columna ya no puede ser VARCHAR(10).
ALTER TABLE usuarios MODIFY contrasena VARCHAR(100) NOT NULL;
