fontfaktor = 1.7;
platte_hoehe = 40;  
platte_breite = 145;  
extrude_height = 2;
offset = 5;
spacing = 8;

    // Box
    color("yellow")
    translate([-platte_breite/2, 0, -extrude_height+0.1])
    cube([platte_breite, platte_hoehe, 2]);

difference() {

    translate([-platte_breite/2, 0, 0])
    color("gray")
    cube([platte_breite, platte_hoehe, 1]);
    
        // Text
    translate([0,offset+spacing*2+1, -1])
    color("gray")
    linear_extrude(height = extrude_height+2) {
        text("Der Gerät!", size = 19/fontfaktor, 
        font = "Segoe Script", halign = "center");
    }


    translate([0,offset+spacing*1, -1])
    color("gray")
    linear_extrude(height = extrude_height+2) {
        text("Ausbildung 2024  -  FI-AE-24", 
        size = 7/fontfaktor, font = "Segoe Script",
        halign = "center");
        
    }

    translate([0,offset+0, -1])
    color("gray")
    linear_extrude(height = extrude_height+2) {
    
    text("Fachinformatiker Anwendungsentwicklung", 
        size = 7/fontfaktor, font = "Segoe Script", 
        halign = "center");
    }
    
}
