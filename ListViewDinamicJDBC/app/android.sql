-- phpMyAdmin SQL Dump
-- version 4.4.12
-- http://www.phpmyadmin.net
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 24-07-2017 a las 14:27:33
-- Versión del servidor: 5.6.25
-- Versión de PHP: 5.6.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `android`
--
CREATE DATABASE IF NOT EXISTS `android` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `android`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

CREATE TABLE IF NOT EXISTS `productos` (
  `id` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `precio` float NOT NULL,
  `cantidad` int(11) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`id`, `nombre`, `precio`, `cantidad`, `descripcion`, `activo`) VALUES
(1, 'cinta abdesiva', 10.5, 6, 'Cinta abdesiva', 1),
(2, 'alicates', 12.75, 10, 'Alicates verdes', 1),
(3, 'destornillador estrella', 4.6, 0, 'destornillador estrella verde', 0),
(4, 'destornillador normal', 4.4, 10, 'destornillador normal verde', 1),
(5, 'cable verde', 5.7, 15, 'cable verde', 1),
(6, 'cable rojo', 5.7, 0, 'cable rojo', 0),
(7, 'cable amarillo', 5.9, 0, 'cable amarillo', 0),
(8, 'partillo pequeño', 10.4, 3, 'martillo pequeño madera', 1),
(9, 'tornillo grande', 2.5, 100, 'tornillo grande acero', 1),
(10, 'tornillo pequeño', 1.7, 200, 'tornillo pequeño acero', 1),
(11, 'tornillo mediano', 2, 150, 'tornillo mediano', 1),
(12, 'martillo mediano', 12.7, 15, 'martillo mediano madera', 1),
(13, 'tenazas', 6, 12, 'tenazas de acero', 1),
(14, 'gafas', 3.2, 0, 'gafas de plastico', 0),
(15, 'pegamento', 2, 50, 'pegamento de contacto', 1),
(16, 'casco', 10.3, 7, 'casco de obra', 1),
(17, 'chaqueta', 23.4, 20, 'chaqueta de obra', 1),
(18, 'guantes', 7.75, 0, 'guantes de obra', 0),
(19, 'botas', 35, 50, 'botas de obra', 1),
(20, 'mallo', 50.4, 0, 'mallo de acero fundido', 0),
(21, 'anclajes', 3.5, 75, 'anchajes de acero', 1),
(22, 'mortero quimico', 32, 40, 'mortero quimico en tubo', 1),
(23, 'pala', 24.2, 0, 'pala de acero y madera', 1),
(24, 'clavos acero', 5, 120, 'clavos de acero', 1),
(25, 'clavos hierro', 3, 0, 'clavos de hierro', 0),
(26, 'tuercas acero', 5.2, 130, 'tuercas de acero', 1),
(27, 'arandela hierro', 3.1, 300, 'arandela de hierro', 1),
(28, 'arandela goma', 1.1, 0, 'arandela de goma', 1),
(29, 'arandela acero', 5.1, 200, 'arandela de acero', 1),
(30, 'llave inglesa', 15.8, 30, 'llave inglesa acero', 1),
(31, 'led rojo', 0.5, 500, 'led de color rojo', 1),
(32, 'led verde', 0.5, 0, 'led de color verde', 0),
(33, 'cinturon', 25.1, 40, 'cinturon de herramientas', 1),
(34, 'metro', 6.6, 40, 'metro de hierro', 1),
(35, 'plomada', 20, 10, 'plomada de acero', 1),
(36, 'cuerda', 10, 42, 'cuerda de nylon', 1);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `productos`
--
ALTER TABLE `productos`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `productos`
--
ALTER TABLE `productos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=37;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
