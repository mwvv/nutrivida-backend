-- Amplía la columna para guardar hashes BCrypt (60 caracteres) en vez de texto plano.
-- La regla de 4 a 10 caracteres sigue aplicando a la contraseña que escribe el usuario, no al hash.
ALTER TABLE usuarios MODIFY contrasena VARCHAR(100) NOT NULL;
