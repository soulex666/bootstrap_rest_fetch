$(document).ready(function () {
    restartAllUser();
});

function restartAllUser() {
    let userTableBody = $("#userTable")
    let currentUserBody = $("#currentUserTable")

    userTableBody.children().remove();
    currentUserBody.children().remove();

    fetch("admin/getusers")
        .then(response => response.json())
        .then(data => data.forEach(function (user) {
            let tableRow = createTable(user);
            userTableBody.append(tableRow);

        })).catch(error => {
        console.log(error);
    });

    fetch("principal")
        .then(response => response.json())
        .then(function (user) {
            let tableRow = currentUserTable(user);
            currentUserBody.append(tableRow);

        }).catch(error => {
        console.log(error);
    });

}

function createTable(user) {
    let userRole = "";
    for (let value of user.roles) {
        userRole += " " + value.role.replaceAll('ROLE_', '');
    }
    return `<tr id="userTable">
            <td>${user.id}</td>
            <td>${user.firstName}</td>
            <td>${user.lastName}</td>
            <td>${user.age}</td>
            <td>${user.username}</td>
            <td>${userRole}</td>
            <td>
            <a  href="/admin/getuser/${user.id}" class="btn btn-info eBtn" >Edit</a>
            </td>
            <td>
            <a  href="/admin/getuser/${user.id}"  class="btn btn-danger dBtn">Delete</a>
            </td>
        </tr>`;
}

function currentUserTable(user) {
    let userRole = "";
    for (let value of user.roles) {
        userRole += " " + value.role.replaceAll('ROLE_', '');
    }

    return `<tr id="currentUserTable">
               <td>${user.id}</td>
                <td>${user.firstName}</td>
                <td>${user.lastName}</td>
                <td>${user.age}</td>
                <td>${user.username}</td>
                <td>${userRole}</td>
            </tr>`;
}

document.addEventListener('click', function (event) {
    event.preventDefault()

    if ($(event.target).hasClass('eBtn')) {
        let href = $(event.target).attr("href");
        $(".editForm #editModal").modal();

        document.getElementById("role1Ed").selected = false;
        document.getElementById("role2Ed").selected = false;

        fetch(href)
            .then(response => response.json())
            .then(function (user, status) {
                $('.editForm #idEdit').val(user.id)
                $('.editForm #firstNameEdit').val(user.firstName)
                $('.editForm #lastNameEdit').val(user.lastName)
                $('.editForm #ageEdit').val(user.age)
                $('.editForm #usernameEdit').val(user.username)
                $('.editForm #passwordEdit').val(user.password)

                user.roles.forEach((role) => {
                    if (role.role === 'ROLE_USER') {
                        document.getElementById("role1Ed").selected = true;
                    }
                    if (role.role === 'ROLE_ADMIN') {
                        document.getElementById("role2Ed").selected = true;
                    }
                });
            })
    }

    if ($(event.target).hasClass('login-Btn')) {
        $(".loginForm #loginModal").modal();
    }

    if ($(event.target).hasClass('editButton')) {
        let user = {
            id: $('#idEdit').val(),
            firstName: $('#firstNameEdit').val(),
            lastName: $('#lastNameEdit').val(),
            age: $('#ageEdit').val(),
            username: $('#usernameEdit').val(),
            password: $('#passwordEdit').val(),
            roles: getRole("#rolesEdit")
        }

        editModalButton(user)
    }

    if ($(event.target).hasClass('addNewUser')) {
        let newUser = {
            id: $('#idNew').val(),
            firstName: $('#firstNameNew').val(),
            lastName: $('#lastNameNew').val(),
            age: $('#ageNew').val(),
            username: $('#usernameNew').val(),
            password: $('#passwordNew').val(),
            roles: getRole("#rolesNew")
        }

        newUserButton(newUser)
    }

    if ($(event.target).hasClass('dBtn')) {
        let href = $(event.target).attr("href");
        $(".deleteForm #deleteModal").modal();

        document.getElementById("role1Del").selected = false;
        document.getElementById("role2Del").selected = false;

        fetch(href)
            .then(response => response.json())
            .then(function (user, status) {
                $('.deleteForm #idDelete').val(user.id)
                $('.deleteForm #firstNameDelete').val(user.firstName)
                $('.deleteForm #lastNameDelete').val(user.lastName)
                $('.deleteForm #ageDelete').val(user.age)
                $('.deleteForm #usernameDelete').val(user.username)

                user.roles.forEach((role) => {
                    if (role.role === 'ROLE_USER') {
                        document.getElementById("role1Del").selected = true;
                    }
                    if (role.role === 'ROLE_ADMIN') {
                        document.getElementById("role2Del").selected = true;
                    }
                });
            })
    }

    if ($(event.target).hasClass('deleteButton')) {
        let id = $('#idDelete').val();
        deleteModalButton(id)
    }

    if ($(event.target).hasClass('logout')) {
        logout();
    }
});

function logout() {
    document.location.replace("/logout");
}

function getRole(rolesEdit) {
    let data = [];
    $(rolesEdit).find("option:selected").each(function () {
        data.push({id: $(this).val(), role: $(this).attr("name"), authority: $(this).attr("name")})
    });
    return data;
}

function editModalButton(user) {
    fetch("/admin/edit", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        body: JSON.stringify(user)
    }).then(function (response) {
        $('input').val('');
        $('.editForm #editModal').modal('hide');

        restartAllUser();
    }).catch(error => {
        console.log(error);
    })
}

function newUserButton(user) {
    fetch("/admin/newuser", {
        method: "POST",
        headers: {
            "Content-Type": "application/json;charset=utf-8"
        },
        body: JSON.stringify(user)
    }).then(function () {
        $('input').val('');
        $('.newUserForm select').val('');
        $('.nav-tabs a[href="#nav-users"]').tab('show');

        restartAllUser();
    }).catch(error => {
        console.log(error);
    })
}

function deleteModalButton(id) {
    fetch("/admin/delete/" + id, {
        method: "DELETE"
    }).then(function () {
        $('.deleteForm #deleteModal').modal('hide');
        restartAllUser();
    }).catch(error => {
        console.log(error);
    })
}
