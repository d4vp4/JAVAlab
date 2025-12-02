CREATE DATABASE CompanyDB;
GO


USE CompanyDB;
GO


-- Таблиця відділів
CREATE TABLE Department (
   id INT PRIMARY KEY IDENTITY(1,1),
   name NVARCHAR(100) NOT NULL
);


-- Таблиця співробітників
CREATE TABLE Employee (
   id INT PRIMARY KEY IDENTITY(1,1),
   name NVARCHAR(100) NOT NULL,
   position NVARCHAR(100),
   department_id INT,
   FOREIGN KEY (department_id) REFERENCES Department(id) ON DELETE SET NULL
);


-- Таблиця завдань
CREATE TABLE Task (
   id INT PRIMARY KEY IDENTITY(1,1),
   description NVARCHAR(255) NOT NULL,
   employee_id INT,
   FOREIGN KEY (employee_id) REFERENCES Employee(id) ON DELETE CASCADE
);


-- Наповнення даними
INSERT INTO Department (name) VALUES ('IT'), ('HR'), ('Sales');


INSERT INTO Employee (name, position, department_id) VALUES
('Ivan Petrenko', 'Developer', 1),
('Oksana Boiko', 'Recruiter', 2),
('Andrii Melnyk', 'Manager', 3);


INSERT INTO Task (description, employee_id) VALUES
('Fix critical bug', 1),
('Interview candidate', 2),
('Prepare report', 3);
GO
