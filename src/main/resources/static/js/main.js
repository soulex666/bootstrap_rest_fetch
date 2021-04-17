$(document).ready(function () {
    $('.table .eBtn').on('click', function (event) {

        event.preventDefault();
        var href = $(this).attr('href');
        $('.editForm #rolesEdit option[value="1"]').attr('selected',false);
        $('.editForm #rolesEdit option[value="2"]').attr('selected',false);

        $.get(href, function (user, status) {
            $('.editForm #idEdit').val(user.id)
            $('.editForm #firstNameEdit').val(user.firstName)
            $('.editForm #lastNameEdit').val(user.lastName)
            $('.editForm #ageEdit').val(user.age)
            $('.editForm #usernameEdit').val(user.username)

            user.roles.forEach((role) => {
                if (role.role === 'ROLE_USER') {
                    $('.editForm #rolesEdit option[value="1"]').attr('selected',true);
                }
                if (role.role === 'ROLE_ADMIN') {
                    $('.editForm #rolesEdit option[value="2"]').attr('selected',true);
                }
            });
        });

        $('.editForm #editModal').modal();
    });
});

$(document).ready(function () {
    $('.table .dBtn').on('click', function (event) {

        event.preventDefault();
        var href = $(this).attr('href');
        $('.deleteForm #rolesDelete option[value="1"]').attr('selected',false);
        $('.deleteForm #rolesDelete option[value="2"]').attr('selected',false);

        $.get(href, function (user, status) {
            $('.deleteForm #idDelete').val(user.id)
            $('.deleteForm #firstNameDelete').val(user.firstName)
            $('.deleteForm #lastNameDelete').val(user.lastName)
            $('.deleteForm #ageDelete').val(user.age)
            $('.deleteForm #usernameDelete').val(user.username)

            user.roles.forEach((role) => {
                if (role.role === 'ROLE_USER') {
                    $('.deleteForm #rolesDelete option[value="1"]').attr('selected',true);
                }
                if (role.role === 'ROLE_ADMIN') {
                    $('.deleteForm #rolesDelete option[value="2"]').attr('selected',true);
                }
            });
        });

        $('.deleteForm #deleteModal').modal();
    });
});
