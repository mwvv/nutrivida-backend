-- Hashea con BCrypt las contraseñas de los usuarios iniciales (V2).
-- admin@duoc.cl      -> admin123
-- cliente@gmail.com  -> cliente123
UPDATE usuarios
SET contrasena = '$2a$10$Y94848pVJR0yxWD.C4TpBefRLCU9LbZepM4SxyPllNqQgjp0.tJVu'
WHERE correo = 'admin@duoc.cl' AND contrasena = 'admin123';

UPDATE usuarios
SET contrasena = '$2a$10$HpUkyC5udjknxvhbEb7eTeRBjx7KrodXj.722wJrW5ba.Q/6tagYS'
WHERE correo = 'cliente@gmail.com' AND contrasena = 'cliente123';
