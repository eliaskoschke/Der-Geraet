thickness = 10;
hole_diameter = 6;  // Durchmesser der zusätzlichen Löcher

// Parameter für die Abschrägung
chamfer_height = 15; // Höhe der Abschrägung
chamfer_width = 35;  // Breite der Abschrägung

// Parameter für die Schraubenvertiefung
screw_diameter = 10; // Durchmesser der Schraube
screw_depth = 3.5;    // Tiefe der Vertiefung
hole_diameter = 6;  // Durchmesser der zusätzlichen Löcher

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