//kick all players()

//admin/adminPanel/rotateStepper - POST
// id in msg -> user ID
// id 0 -> Ursprung

// .../activateCardMotor -> POST
// message -> leer


function rotateStepper(id) {
    console.log(post('admin/adminPanel/rotateStepper', id));
}

function activateCardMotor() {
    console.log(post('admin/adminPanel/activateCardMotor', ''));
}

function back() {
    window.location.replace('admin.html?loggedIn=true');
}