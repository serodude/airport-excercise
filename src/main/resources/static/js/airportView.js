let api = 'http://localhost:8080/api/planes';
let selectedAirport;
let currentRecord;

$(document).ready(function() {
    initDataTable(api);

    $("#refreshBtn").click(getData);

    $("#deleteButton").click();

    $("#addBtn").on('click', function() {
        document.getElementById("modal-title").innerHTML = "Add a new Plane";
        document.getElementById("modalForm").reset();
        $("#btnsubmit").attr('onclick', 'handleNewSubmit();');
        $('#postDetail').modal('toggle');
        $("#isVisiting").attr('checked', false);
        $('#modalForm').removeClass("was-validated");
        getAirport(1);
    });
});

function initDataTable(api) {

    columns = [
        { "data": "id", "title": "id" },
        { "data": "name", "title": "Name" },
        { "data": "fuel", "title": "Fuel" },
        {
            "data": "airport",
            "title": "Airport",
            "render": function(data) {
                return data['name']
            }
        }
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

function getFormData() {
    let formObj = {
        id: parseInt($("#id").val()),
        name: $("#name").val(),
        fuel: parseInt($("#fuel").val()),
        airport: selectedAirport,

    };
    console.log(formObj);
    return formObj;
}

function getAirport(id) {
    $.ajax({
        url: "http://localhost:8080/api/airports/" + id,
        type: "get",
        success: function(result) {
            $("#btnsubmit").attr('disabled', false);
            selectedAirport = result;
            return result;
        }
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

    var formData = getFormData();

    console.log(JSON.stringify(formData));

    $.post({
        url: api + "/add",
        data: JSON.stringify(formData),
        contentType: "application/json",
        success: getData,
        error: function(error) {
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
    $("#name").val(record.name);
    $("#fuel").val(record.fuel);
    $("#airport").val(record.airport.id);

    selectedAirport = record.airport;
    currentRecord = record;
}

function submitEdit(id, api) {

    // shortcut for filling the formData as a JavaScript object with the fields in the form
    var formData = getFormData();

    console.log("Updating row with id:" + id)
    $.ajax({
        url: api + "/" + id,
        type: "put",
        data: JSON.stringify(formData),
        contentType: "application/json",
        success: getData,
        error: function(error) {
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

$("#airport").on("change", function() {
    $("#btnsubmit").attr('disabled', true);
    getAirport($(this).val());
});

function flyto(id) {

    $.ajax({
        url: "http://localhost:8080/api/planes/flyto/" + currentRecord.id + "/" + id,
        type: "get",
        success: function() {
            getData();
            deselect();
            $('#postDetail').modal('toggle');
        },
        error: function(result) {
            alert(result.responseJSON.msg);
        }
    });

    // console.log("fly to " + id);
    // if (id == currentRecord.airport.id) {
    //     alert("This plane is already at the given airport");
    //     return;
    // }
    // if (currentRecord.fuel < 2) {
    //     alert("This plane has not enough fuel left to flye");
    //     return;
    // }

    // $.ajax({
    //     url: "http://localhost:8080/api/airports/" + id,
    //     type: "get",
    //     success: function(result) {
    //         flyPlane(result);
    //     }
    // });
}

function flyPlane(airport) {
    var formData = currentRecord;
    formData.airport = airport;
    formData.fuel -= 2;

    updatePlane(formData);

    deselect();
    $('#postDetail').modal('toggle');
}

function refuel() {
    console.log("do some refueling");
    if (currentRecord.fuel == 5) {
        alert("Plane is already full of fuel!");
        return;
    }
    // shortcut for filling the formData as a JavaScript object with the fields in the form
    var formData = currentRecord;
    formData.fuel = 5;

    updatePlane(formData);

    deselect();
    $('#postDetail').modal('toggle');

}

function updatePlane(plane) {
    console.log("Updating row with id:" + id)
    $.ajax({
        url: api + "/" + currentRecord.id,
        type: "put",
        data: JSON.stringify(plane),
        contentType: "application/json",
        success: getData,
        error: function(error) {
            console.log(error);
        }
    });
}