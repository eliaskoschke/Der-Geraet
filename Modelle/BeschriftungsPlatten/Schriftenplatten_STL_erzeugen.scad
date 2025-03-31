fontfaktor = 2.2;
platte_hoehe = 40;  
platte_breite = 110;  
extrude_height = 2;
offset = 6;
spacing = 8;
color1 ="black";
color2 ="white";



    // Box
    basisbox();

    schriftDerGeraet();
    //schriftUnteschriften1();
    //schriftUnteschriften2();

/*
difference() {

    translate([-platte_breite/2, 0, 0])
    color("white")
    cube([platte_breite, platte_hoehe, 1]);
    
    //schriftDerGeraet();
    schriftUnteschriften1();
    //schriftUnteschriften2();
}
*/

module basisbox() {
    color(color1)
    translate([-platte_breite/2, 0, -extrude_height+0.1])
    cube([platte_breite, platte_hoehe, 2]);
}


module schriftUnteschriften1() {
    color(color2)
    translate([-platte_breite/2, 0, 0])
linear_extrude(height = extrude_height)
import("D:/DEV_SCHUL/Projekt_CardDealer/Getriebe 3D/Schriften/schrift_dergeraet_unterschriften_1_nur_platte.svg");
}


module schriftUnteschriften2() {
    color(color2)
    translate([-platte_breite/2, 0, 0])
linear_extrude(height = extrude_height)
import("D:/DEV_SCHUL/Projekt_CardDealer/Getriebe 3D/Schriften/schrift_dergeraet_unterschriften_2_nur_platte.svg");
}




module schriftDerGeraet() {

    // Text
    translate([0,offset+spacing*2+1, -1])
    color(color2)
    linear_extrude(height = extrude_height) {
        text("Der Gerät!", size = 19/fontfaktor, 
        font = "Segoe Script", halign = "center");
    }

    translate([0,offset+2, -1])
    color(color2)
    linear_extrude(height = extrude_height) {
        text("FI-AE-24", 
        size = 19/fontfaktor, font = "Segoe Script",
        halign = "center");
        
    }

/*    translate([0,offset+0, -1])
    color(color2)
    linear_extrude(height = extrude_height) {
    
    text("Fachinformatiker Anwendungsentwicklung", 
        size = 7/fontfaktor, font = "Segoe Script", 
        halign = "center");
    }*/
}