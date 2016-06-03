function validateForm() {
    var price = document.forms["frm"]["price"].value;
    if (price == null || price == "" || !(isNaN(price))) {
        alert("Please enter valid price.");
        return false;
    }
    var description = document.forms["frm"]["description"].value;
    if (description == null || description == "") {
        alert("Please enter description.");
        return false;
    }
}
