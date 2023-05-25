$(document).ready(function() {
    $("#buttonCancel").on("click", function() {
        window.location = moduleURL;
    });

    $("#fileImage").change(function() {
        if (!checkFileSize(this)) {
            return;
        }

        showImageThumbnail(this);
    })
});

function showImageThumbnail(fileInput) {
    let file = fileInput.files[0];
    let reader = new FileReader();
    reader.onload = function(e) {
        $("#thumbnail").attr("src", e.target.result);
    }

    reader.readAsDataURL(file);
}

function checkFileSize(fileInput) {
    let fileSize = fileInput.files[0].size;

    if (MAX_FILE_SIZE < fileSize) {
        fileInput.setCustomValidity("You must choose an image less than " + MAX_FILE_SIZE + "bytes!");
        fileInput.reportValidity();

        return false;
    } else {
        fileInput.setCustomValidity("");

        return true;
    }
}

function showModalDialog(title, message) {
    $("#modalTitle").text(title);
    $("#modalBody").text(message);
    $("#modalDialog").modal();
}

function showErrorModal(message) {
    showModalDialog("Error", message);
}

function showWarningModal(message) {
    showModalDialog("Warning", message);
}