let extraImagesCount = 0;
let dropdownBrands = $("#brand");
let dropdownCategories = $("#category");

$(document).ready(function() {
    $("#shortDescription").richText();
    $("#fullDescription").richText();

    dropdownBrands.change(function() {
        dropdownCategories.empty();
        getCategories();
    })

    getCategories();

    $("input[name='extraImage']").each(function(index) {
        extraImagesCount++;

        $(this).change(function() {
            showExtraImageThumbnail(this, index);
        })
    })
})

function showExtraImageThumbnail(fileInput, index) {
    let file = fileInput.files[0];
    let reader = new FileReader();
    reader.onload = function(e) {
        $("#extraThumbnail" + index).attr("src", e.target.result);
    }

    reader.readAsDataURL(file);

    if (extraImagesCount - 1 <= index) {
        addNextExtraImageSection(index + 1);
    }
}

function addNextExtraImageSection(index) {
    let htmlExtraImage = `
        <div class="col border m-3 p-2" id="divExtraImage${index}">
            <div id="extraImageHeader${index}">
                <label>Extra Image #${index + 1}:</label>
            </div>
            <div class="m-2">
                <img id="extraThumbnail${index}" alt="Extra image #${index + 1} preview" class="img-fluid"
                     src="${defaultImageThumbnailSrc}"/>
            </div>
            <div>
                <input type="file" name="extraImage" accept="image/png, image/jpeg"
                    onchange="showExtraImageThumbnail(this, ${index})"/>
            </div>
        </div>
    `

    let htmlLinkRemove = `
        <a class="btn fas fa-times-circle fa-2x icon-dark float-right" 
            href="javascript:removeExtraImage(${index - 1})"
            title="Remove this image">         
        </a>
    `

    $("#divProductImages").append(htmlExtraImage);
    $("#extraImageHeader" + (index - 1)).append(htmlLinkRemove);

    extraImagesCount++;
}

function removeExtraImage(index) {
    $("#divExtraImage" + index).remove();
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