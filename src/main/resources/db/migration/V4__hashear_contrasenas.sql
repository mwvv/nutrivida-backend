-- Reemplaza las contraseñas en texto plano de V2 por su hash BCrypt (cost 10).
UPDATE usuarios SET contrasena = '$2a$10$CbFrw27roAA/L0k0n90o2e3FeKRSw.7sR2t3F8lhm3SsOCf8.H98y' WHERE correo = 'admin@duoc.cl';
UPDATE usuarios SET contrasena = '$2a$10$M7wsS.xJrOMHOLG.h/dM8OdNii25c4SvO36jJ3iReE68jN5fCIt4u' WHERE correo = 'cliente@gmail.com';
