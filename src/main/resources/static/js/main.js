$(document).ready(function () {
    restartAllUser();
});


function restartAllUser() {
    let UserTableBody = $("#userTable")

    UserTableBody.children().remove();

    fetch("admin/getusers")
        .then((response) => {
            response.json().then(data => data.forEach(function (item) {
                let TableRow = createTable(item);
                UserTableBody.append(TableRow);

            }));
        }).catch(error => {
        console.log(error);
    });
}

function createTable(user) {
    let userRole = "";
    for (let i = 0; i < user.roles.length; i++) {
        userRole += " " + user.roles[i].role.replaceAll('ROLE_', '');
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

document.addEventListener('click', function (event) {
    event.preventDefault()

    if ($(event.target).hasClass('eBtn')) {
        let href = $(event.target).attr("href");
        $(".editForm #editModal").modal();

        $('.editForm #rolesEdit option[value="1"]').attr('selected', false);
        $('.editForm #rolesEdit option[value="2"]').attr('selected', false);

        fetch(href)
            .then(responce => responce.json())
            .then(function (user, status) {
                $('.editForm #idEdit').val(user.id)
                $('.editForm #firstNameEdit').val(user.firstName)
                $('.editForm #lastNameEdit').val(user.lastName)
                $('.editForm #ageEdit').val(user.age)
                $('.editForm #usernameEdit').val(user.username)
                $('.editForm #passwordEdit').val(user.password)

                user.roles.forEach((role) => {
                    if (role.role === 'ROLE_USER') {
                        $('.editForm #rolesEdit option[value="1"]').attr('selected', true);
                    }
                    if (role.role === 'ROLE_ADMIN') {
                        $('.editForm #rolesEdit option[value="2"]').attr('selected', true);
                    }
                });
            })
    }

    if ($(event.target).hasClass('editButton')) {
        let user = {
            id:$('#idEdit').val(),
            firstName:$('#firstNameEdit').val(),
            lastName:$('#lastNameEdit').val(),
            age:$('#ageEdit').val(),
            username:$('#usernameEdit').val(),
            password:$('#passwordEdit').val(),
            roles: getRole("#rolesEdit")
        }

        editModalButton(user)
        console.log(user);
    }

    if ($(event.target).hasClass('logout')) {
        logout();
    }
});

function logout(){
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
    })
}




function deleteUser() {
    $('.dBtn').on('click', function (event) {

        event.preventDefault();
        var href = $(this).attr('href');
        $('.deleteForm #rolesDelete option[value="1"]').attr('selected', false);
        $('.deleteForm #rolesDelete option[value="2"]').attr('selected', false);

        fetch(href)
            .then(responce => responce.json())
            .then(function (user, status) {
                $('.deleteForm #idDelete').val(user.id)
                $('.deleteForm #firstNameDelete').val(user.firstName)
                $('.deleteForm #lastNameDelete').val(user.lastName)
                $('.deleteForm #ageDelete').val(user.age)
                $('.deleteForm #usernameDelete').val(user.username)

                user.roles.forEach((role) => {
                    if (role.role === 'ROLE_USER') {
                        $('.deleteForm #rolesDelete option[value="1"]').attr('selected', true);
                    }
                    if (role.role === 'ROLE_ADMIN') {
                        $('.deleteForm #rolesDelete option[value="2"]').attr('selected', true);
                    }
                });
            })
            .then($('.deleteForm #deleteModal').modal())
    });
}
