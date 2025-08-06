function updateTotal() {
    let total = 0;
    <% for (VolSiege volSiege : volSieges) { %>
        <% 
            PrixVol prixVol = prixVols.stream()
                .filter(p -> p.getTypeSiege().getId() == volSiege.getTypeSiege().getId())
                .findFirst()
                .orElse(null);
            double prix = prixVol != null ? prixVol.getPrix() : 0.0;
        %>
        let input_<%= volSiege.getTypeSiege().getId() %> = document.querySelector('input[name="nombre_<%= volSiege.getTypeSiege().getId() %>"]');
        if (input_<%= volSiege.getTypeSiege().getId() %>) {
            let count = parseInt(input_<%= volSiege.getTypeSiege().getId() %>.value) || 0;
            total += count * <%= prix %>;
        }
    <% } %>
    document.getElementById('totalPrice').textContent = total.toFixed(2);
}

function showConfirmation() {
    let details = "";
    let total = 0;
    <% for (VolSiege volSiege : volSieges) { %>
        <% 
            PrixVol prixVol = prixVols.stream()
                .filter(p -> p.getTypeSiege().getId() == volSiege.getTypeSiege().getId())
                .findFirst()
                .orElse(null);
            double prix = prixVol != null ? prixVol.getPrix() : 0.0;
        %>
        let input_<%= volSiege.getTypeSiege().getId() %> = document.querySelector('input[name="nombre_<%= volSiege.getTypeSiege().getId() %>"]');
        if (input_<%= volSiege.getTypeSiege().getId() %> && parseInt(input_<%= volSiege.getTypeSiege().getId() %>.value) > 0) {
            let count = parseInt(input_<%= volSiege.getTypeSiege().getId() %>.value);
            details += `${count} <%= volSiege.getTypeSiege().getNom() %>(s) à <%= String.format("%.2f", prix) %> Ar chacun, `;
            total += count * <%= prix %>;
        }
    <% } %>
    if (details === "") {
        alert("Veuillez sélectionner au moins une place à réserver.");
        return;
    }
    details += `Total: ${total.toFixed(2)} Ar`;
    document.getElementById('confirmationDetails').textContent = details;
    document.getElementById('confirmationPopup').classList.add('show');
    document.getElementById('confirmationOverlay').classList.add('show');
}

function hideConfirmation() {
    document.getElementById('confirmationPopup').classList.remove('show');
    document.getElementById('confirmationOverlay').classList.remove('show');
}

function submitReservation() {
    let params = [];
    <% for (VolSiege volSiege : volSieges) { %>
        let input_<%= volSiege.getTypeSiege().getId() %> = document.querySelector('input[name="nombre_<%= volSiege.getTypeSiege().getId() %>"]');
        if (input_<%= volSiege.getTypeSiege().getId() %> && parseInt(input_<%= volSiege.getTypeSiege().getId() %>.value) > 0) {
            params.push('<%= volSiege.getTypeSiege().getId() %>:' + parseInt(input_<%= volSiege.getTypeSiege().getId() %>.value));
        }
    <% } %>
    if (params.length === 0) {
        alert("Veuillez sélectionner au moins une place à réserver.");
        return;
    }
    let form = document.getElementById('reservationForm');
    let input = document.createElement('input');
    input.type = 'hidden';
    input.name = 'allParams';
    input.value = params.join(',');
    form.appendChild(input);

    // Submit form for reservation
    form.submit();

    // Trigger PDF generation
    let pdfForm = document.createElement('form');
    pdfForm.action = '<%= request.getContextPath() %>/user-vol/generate-pdf';
    pdfForm.method = 'POST';
    pdfForm.style.display = 'none';
    let pdfInputVolId = document.createElement('input');
    pdfInputVolId.type = 'hidden';
    pdfInputVolId.name = 'volId';
    pdfInputVolId.value = '<%= vol.getId() %>';
    let pdfInputUtilisateurId = document.createElement('input');
    pdfInputUtilisateurId.type = 'hidden';
    pdfInputUtilisateurId.name = 'utilisateurId';
    pdfInputUtilisateurId.value = '<%= user.getId() %>';
    let pdfInputParams = document.createElement('input');
    pdfInputParams.type = 'hidden';
    pdfInputParams.name = 'allParams';
    pdfInputParams.value = params.join(',');
    pdfForm.appendChild(pdfInputVolId);
    pdfForm.appendChild(pdfInputUtilisateurId);
    pdfForm.appendChild(pdfInputParams);
    document.body.appendChild(pdfForm);
    pdfForm.submit();
}