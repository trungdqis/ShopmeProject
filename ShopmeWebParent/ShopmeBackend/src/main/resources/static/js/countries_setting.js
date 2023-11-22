let buttonLoad;
let dropdownCountries;
let buttonAddCountry;
let buttonUpdateCountry;
let buttonDeleteCountry;
let labelCountryName;
let fieldCountryName;
let fieldCountryCode;

$(document).ready(function() {
    buttonLoad = $("#buttonLoadCountries");
    dropdownCountries = $("#dropdownCountries");
    buttonAddCountry = $("#buttonAddCountry");
    buttonUpdateCountry = $("#buttonUpdateCountry");
    buttonDeleteCountry = $("#buttonDeleteCountry");
    labelCountryName = $("#labelCountryName");
    fieldCountryName = $("#fieldCountryName");
    fieldCountryCode = $("#fieldCountryCode");

    buttonLoad.click(function() {
        loadCountries();
    })

    dropdownCountries.on("change", function() {
        changeFormStateToSelectedCountry();
    })

    buttonAddCountry.click(function() {
        if ("Add" === buttonAddCountry.val()) {
            addCountry();
        } else {
            changeFormStateToNew();
        }
    })

    buttonUpdateCountry.click(function() {
        updateCountry();
    })

    buttonDeleteCountry.click(function() {
        deleteCountry();
    })
})

function deleteCountry() {
    let optionValue = dropdownCountries.val();
    let countryId = optionValue.split("-")[0];
    let url = contextPath + "countries/delete/" + countryId;

    $.get(url, function() {
        $("#dropdownCountries option[value='" + optionValue + "']").remove();
        changeFormStateToNew();
    }).done(function() {
        showToastMessage("The country has been deleted");
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    })
}

function updateCountry() {
    let url = contextPath + "countries/save";
    let countryName = fieldCountryName.val();
    let countryCode = fieldCountryCode.val();
    let countryId = dropdownCountries.val().split("-")[0];
    let jsonData = {id: countryId, name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function(countryId) {
        $("#dropdownCountries option:selected").val(countryId + "-" + countryCode);
        $("#dropdownCountries option:selected").text(countryName);
        showToastMessage("The country has been updated");

        changeFormStateToNew();
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    })
}

function addCountry() {
    let url = contextPath + "countries/save";
    let countryName = fieldCountryName.val();
    let countryCode = fieldCountryCode.val();
    let jsonData = {name: countryName, code: countryCode};

    $.ajax({
        type: 'POST',
        url: url,
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeaderName, csrfValue);
        },
        data: JSON.stringify(jsonData),
        contentType: 'application/json'
    }).done(function(countryId) {
        selectNewlyAddedCountry(countryId, countryCode, countryName);
        showToastMessage("The new country has been added");
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    })
}

function selectNewlyAddedCountry(countryId, countryCode, countryName) {
    let optionValue = countryId + "-" + countryCode;
    $("<option>").val(optionValue).text(countryName).appendTo(dropdownCountries);

    $("#dropdownCountries option[value='" + optionValue + "']").prop("selected", true);

    fieldCountryCode.val("");
    fieldCountryName.val("").focus();
}

function changeFormStateToNew() {
    buttonAddCountry.val("Add");
    labelCountryName.text("Country Name");

    buttonUpdateCountry.prop("disabled", true);
    buttonDeleteCountry.prop("disabled", true);

    fieldCountryCode.val("");
    fieldCountryName.val("").focus();
}

function changeFormStateToSelectedCountry() {
    buttonAddCountry.prop("value", "New");
    buttonUpdateCountry.prop("disabled", false);
    buttonDeleteCountry.prop("disabled", false);

    labelCountryName.text("Selected Country:");
    let selectedCountryName = $("#dropdownCountries option:selected").text();
    fieldCountryName.val(selectedCountryName);

    let countryCode = dropdownCountries.val().split("-")[1];
    fieldCountryCode.val(countryCode);
}

function loadCountries() {
    let url = contextPath + "countries/list";

    $.get(url, function(responseJson) {
        dropdownCountries.empty();

        $.each(responseJson, function(index, country) {
            let optionValue = country.id + "-" + country.code;
            $("<option>").val(optionValue).text(country.name).appendTo(dropdownCountries);
        })
    }).done(function() {
        buttonLoad.val("Refresh Country List");
        showToastMessage("All countries have been loaded");
    }).fail(function() {
        showToastMessage("ERROR: Could not connect to server or server encountered an error");
    })
}

function showToastMessage(message) {
    $("#toastMessage").text(message);
    $(".toast").toast('show');
}