$fn= 60;
hoehe= -3.5;

//button
module button()
{
difference()
{
union()
{
rotate_extrude(convexity = 10)
	translate([10.5, 0, 0])
		circle(r = 1.5);
translate ([0,0,-1.5])
	cylinder (r=10.5, h=3);
translate ([0,0,-3])
	cylinder (r=12, h=3);
translate ([0,0,-4.5])
	cylinder (r=13, h=3);
}
translate ([0,0,5.1])
	scale ([1,1,0.25])
		sphere (r=18);
}
}
//translate ([0,0,1.5])
//button();

module buttoncase()
{
difference()
{
union()
{

translate([0, 0, -1.5])
rotate_extrude(convexity = 10)
	translate([14.5, 0, 0])
		circle(r = 3);
translate ([0,0,0])
	cylinder (r=14.5, h=1.5);
translate ([0,0,-2.5])
	cylinder (r=17.5, h=1);

}
translate ([0,0,-17.4])
	cylinder (r=17.7, h=16);
translate ([0,0,-1])
	cylinder (r=12.15, h=4.5);
translate ([0,0,-24])
	cylinder (r=13.15, h=24.5);

}
//body
difference()
{
translate ([0,0,-17.4])
	cylinder (r=15, h=16);
translate ([0,0,-24])
	cylinder (r=13.15, h=24.5);
//trislot goes here---
translate ([0,0,0.5 + hoehe])
switchholder();
translate ([0,0,0.5 + hoehe])
	rotate ([0,0,10])
		switchholder();

translate ([0,0,-14 + (hoehe/2)])
	cube ([3,36,14 + hoehe],center=true);
translate ([0,0,-7+ (hoehe)])
 rotate ([0,0,5])
	cube ([2.5,36,3],center=true);
translate ([0,0,-14+ (hoehe/2)])
	cube ([36,3,14+hoehe],center=true);
translate ([0,0,-7+ (hoehe)])
 rotate ([0,0,5])
		cube ([36,2.5,3],center=true);
}
}
//buttoncase();

module switch()
{
translate ([0,0,0])
	cube ([11.95,11.95,3.7],center=true);
translate ([0,0,1.85])
	cylinder (r=3.9, h=0.66);
//locator pins
translate ([4.47,0,-3.10])
	cylinder (r=0.75, h=1.3);
translate ([-4.47,0,-3.10])
	cylinder (r=0.75, h=1.3);
// terminals
for (i=[[3,5.8,-2.6],[-3,5.8,-2.6],[-3,-5.8,-2.6],[3,-5.8,-2.6]])
translate (i)
	cube ([2,1,4.5],center=true);
}






module switchholder()
{
difference()
{
union()
{
translate ([0,0,-9])
	cylinder (r=13, h=3);
//locking pins
translate ([0,15,-7.5])
	rotate ([90,0,0])
		cylinder (r=1.5, h=30);
translate ([-15,0,-7.5])
	rotate ([0,90,0])
		cylinder (r=1.5, h=30);
}
scale ([1.02,1.02,1.01])
translate ([0,0,-5.6])
	switch();
translate([8,8,-10])
    cylinder(r=7, h=10);
}
}

//put it all together
translate ([0,0,1.5])
	button();
translate ([0,0,-5.6])
	switch();
translate([0,0,0 + hoehe])
    switchholder();

translate ([0,0,-0.5])
	buttoncase();































































