let api = 'http://localhost:8080/api/visitors';

$(document).ready(function() {
    initDataTable(api);

    $("#refreshBtn").click(getData);

    $("#deleteButton").click();

    $("#addBtn").on('click', function() {
        document.getElementById("modal-title").innerHTML = "Add a new Visitor";
        document.getElementById("modalForm").reset();
        $("#btnsubmit").attr('onclick', 'handleNewSubmit();');
        $('#postDetail').modal('toggle');
        $("#isVisiting").attr('checked', false);
        $('#modalForm').removeClass("was-validated");

    });
});

function initDataTable(api) {

    columns = [
        { "data": "id", "title": "id" },
        { "data": "firstName", "title": "firstname" },
        { "data": "lastName", "title": "lastname" },
        { "data": "birthdate", "title": "date of birth" },
        { "data": "city", "title": "city" },
        { "data": "visiting", "title": "is visiting" }
    ];

    // how simple it is to create a datatable :-)
    let table = $('#dataTable').DataTable({
        "order": [
            [0, "asc"]
        ],
        "ajax": {
            url: api,
            dataSrc: ''
        },
        "columns": columns
    });

    $('#dataTable tbody').on('click', 'tr', function() {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
        }
        deselect();
        $(this).addClass('selected');
        var table = $('#dataTable').DataTable();
        var data = table.row(this).data();

        // this function fetches one record and fill the modal with the data and shows the modal for editing
        console.log(data);
        getSingleRecord(data.id, api);

        $('#postDetail').modal('toggle');
    });
}

function clear() {
    $("#dataTable").DataTable().clear();
    $("#dataTable").DataTable().columns.adjust().draw();
}

function getData() {
    $("#dataTable").dataTable().api().ajax.reload();
    let data = $("#dataTable").dataTable().api().column(0).data();
    console.log(data);
    let sum = 0;
    for (let i = 0; i < data.length; i++) {
        sum += +data[i];
    }
}

function getSingleRecord(id, api) {

    apiPath = String(api + "/" + id);
    $.get(apiPath, function(data) {
        if (data) {
            fillUpdateDiv(data, api);
        }
    });
}


function submitNew() {
    console.log("Add new room");

    var formData = $("#modalForm").serializeArray().reduce(function(result, object) { result[object.name] = object.value; return result }, {});
    for (var key in formData) {
        if (formData[key] == "" || formData == null) delete formData[key];
    }
    formData["visiting"] = $("#isVisiting").is(":checked");

    console.log(JSON.stringify(formData));

    $.post({
        url: api + "/add",
        data: JSON.stringify(formData),
        contentType: "application/json",
        success: getData,
        error: function(error) {
            alert("There are already 5 people visiting!");
            getData();
            console.log(error);
        }
    });

    deselect();
    $('#postDetail').modal('toggle');
}

// this function perform cleaning up of the table
// 1. remove eventually selected class
// 2. clean the form using the reset method
function deselect() {

    $('#dataTable tr.selected').removeClass('selected');

    document.getElementById("modalForm").reset();
}



function fillUpdateDiv(record, api) {

    $("#btnsubmit").attr('onclick', 'handleEditSubmit(' + record.id + ', "' + api + '");');
    $("#deleteButton").attr('onclick', 'submitDelete(' + record.id + ', "' + api + '");');

    document.getElementById("modal-title").innerHTML = "Edit a table";

    // this function fills the modal
    fillModal(record);

}

//  show the usage of the popover here!
function fillModal(record) {
    $('#modalForm').removeClass("was-validated");
    // fill the modal
    $("#id").val(record.id);
    $("#firstName").val(record.firstName);
    $("#lastName").val(record.lastName);
    $("#birthdate").val(record.birthdate);
    $("#city").val(record.city);
    $("#isVisiting").attr('checked', record.visiting);
    //val(record.visiting);


}

function submitEdit(id, api) {

    // shortcut for filling the formData as a JavaScript object with the fields in the form
    var formData = $("#modalForm").serializeArray().reduce(function(result, object) { result[object.name] = object.value; return result }, {});
    console.log("Formdata =>");
    console.log(formData);
    for (var key in formData) {
        if (formData[key] == "" || formData == null) delete formData[key];
    }

    formData["visiting"] = $("#isVisiting").is(":checked");

    console.log("Updating row with id:" + id)
    $.ajax({
        url: api + "/" + id,
        type: "put",
        data: JSON.stringify(formData),
        contentType: "application/json",
        success: getData,
        error: function(error) {
            alert("There are already 5 people visiting!");
            console.log(error);
        }
    });

    deselect();
    $('#postDetail').modal('toggle');
}

function submitDelete(id, api) {

    console.log(`Deleting row with id: ${id}`);
    $.ajax({
        url: api + "/" + id,
        type: "delete",
        dataType: 'application/json',
        success: getData,
    });

    $('#postDetail').modal('toggle');
}

function handleEditSubmit(id, api) {
    var forms = document.getElementsByClassName('needs-validation');
    var validation = Array.prototype.filter.call(forms, function(form) {
        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
        } else {
            event.preventDefault();
            submitEdit(id, api);
        }
        form.classList.add('was-validated');
    });
}

function handleNewSubmit() {
    var forms = document.getElementsByClassName('needs-validation');
    var validation = Array.prototype.filter.call(forms, function(form) {
        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
        } else {
            event.preventDefault();
            submitNew();
        }
        form.classList.add('was-validated');
    });
}