let dropdownBrands = $("#brand");
let dropdownCategories = $("#category");

$(document).ready(function() {
    $("#shortDescription").richText();
    $("#fullDescription").richText();

    dropdownBrands.change(function() {
        dropdownCategories.empty();
        getCategories();
    })

    getCategoriesForNewForm();
})

function getCategoriesForNewForm() {
    let categoryIdField = $("#categoryId");
    let editMode = false;

    if (categoryIdField.length) {
        editMode = true;
    }

    if (!editMode) {
        getCategories();
    }
}

function getCategories() {
    let brandId = dropdownBrands.val();
    let url = brandModuleURL + "/" + brandId + "/categories";

    $.get(url, function(responseJson) {
        $.each(responseJson, function(index, category) {
            $("<option>").val(category.id).text(category.name).appendTo(dropdownCategories);
        })
    })
}

function checkUnique(form) {
    let productId = $("#id").val();
    let productName = $("#name").val();
    let csrfValue = $("input[name='_csrf']").val();

    let params = {id: productId, name: productName, _csrf: csrfValue};

    $.post(checkUniqueUrl, params, function(response) {
        if ("OK" === response) {
            form.submit();
        } else if ("Duplicate" === response) {
            showWarningModal("There is another product having same name " + productName);
        } else {
            showErrorModal("Unknown response from the server")
        }
    }).fail(function() {
        showErrorModal("Could not connect to the server")
    })

    return false;
}