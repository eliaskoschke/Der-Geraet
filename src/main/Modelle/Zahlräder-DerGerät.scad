// Zahnradgetriebe-Modell V4
// Autor: Roberto Rahl mit CAD-Expertensystem
// Runtime: OpenSCAD 'version 2021.01'
// Datum: 18.2.2025
// Quellen: MCAD/involute_gears.scad

use <MCAD/involute_gears.scad>

// Hauptparameter
outer_diameter = 218;
thickness = 10;
big_teeth = 93;
small_teeth = 15;
offset = 86;
wall_thickness = 8; 

// Parameter für die Motorachse
shaft_diameter = 5; // Durchmesser der Motorachse
flat_percent = 24; // Abflachung in Prozent (0-50)

// Parameter für die Schraubenvertiefung
screw_diameter = 10; // Durchmesser der Schraube
screw_depth = 3.5; // Tiefe der Vertiefung
hole_diameter = 6; // Durchmesser der zusätzlichen Löcher

// Parameter für die Abschrägung
chamfer_height = 15; // Höhe der Abschrägung
chamfer_width = 35; // Breite der Abschrägung

// Halterungsmodul mit Schraubenvertiefung, zusätzlichem Loch und Abschrägung
module mount() {
difference() {
hull() {
cube([1, 15, thickness], center=true);
translate([10, 0, 0])
cylinder(d=15, h=thickness, center=true, $fn=32);
}

// Vertiefung für die Schraube
translate([10, 0, thickness/2 - screw_depth/2])
cylinder(d=screw_diameter, h=screw_depth, center=true, $fn=32);

// Bohrloch für die Schraube
translate([10, 0, 0])
cylinder(d=hole_diameter, h=thickness+2, center=true, $fn=32);

// Abschrägung durch Subtraktion eines schrägen Würfels
translate([25 - chamfer_width/2, 0, thickness/2 + chamfer_height/2])
rotate([15, 0, 90])
cube([chamfer_width, 25, chamfer_height], center=true);
}
}

// Modul für das äußere Zahnrad
module outer_gear() {
difference() {
union() {
// Äußerer Ring
difference() {
cylinder(d=outer_diameter, h=thickness, $fn=100);
translate([0,0,-1])
cylinder(d=outer_diameter-2*wall_thickness, h=thickness+2, $fn=100);
}

// Halterungen
for(i = [0:90:359]) {
rotate([0, 0, i])
translate([outer_diameter/2 - 5, 0, thickness/2])
mount();
}
}

// Innenverzahnung
translate([0,0,-1])
gear(number_of_teeth=big_teeth,
circular_pitch=400,
gear_thickness=thickness+2,
rim_thickness=thickness+2,
hub_thickness=thickness+2,
bore_diameter=0,
internal=true,
pressure_angle=20);
}
}

// Modul für die abgeflachte Bohrung
module flat_shaft_hole(diameter, height, flat_percent) {
flat_depth = (diameter * flat_percent / 100);
intersection() {
cylinder(d=diameter, h=height, $fn=32);
translate([diameter/2 - flat_depth, 0, height/2])
cube([diameter, diameter, height+1], center=true);
}
}

// Modul für das Antriebszahnrad
module drive_gear() {
difference() {
union() {
// Basis-Zahnrad
gear(number_of_teeth=small_teeth,
circular_pitch=400,
gear_thickness=thickness,
rim_thickness=thickness,
hub_thickness=thickness,
bore_diameter=0); // Bohrung auf 0 gesetzt

// Verstärkungszylinder
translate([0,0,thickness])
cylinder(d=15, h=5, $fn=32);
}

// Abgeflachte Bohrung durch alles
flat_shaft_hole(shaft_diameter, thickness+5+1, flat_percent);
}
}

// Zusammenbau der Zahnräder ("Main")
outer_gear();
translate([offset,0,0]) drive_gear();
//translate([offset-20,0,0]) drive_gear(); // Für 3D -Druck

 