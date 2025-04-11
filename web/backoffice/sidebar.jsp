<div class="sidebar">
    <div class="sidebar-header">
        <h3><i class="fa-solid fa-plane sidebar-logo"></i> Air Manager</h3>
    </div>
    
    <div class="sidebar-menu">
        
        <div class="menu-item has-submenu open">
            <a href="#">
                <i class="fa-solid fa-plane-departure menu-icon"></i>
                <span class="menu-text">Vol</span>
            </a>
            <ul class="submenu open">
                <li><a href="<%= request.getContextPath() %>/vol/insert" class="current">Insertion</a></li>
                <li><a href="<%= request.getContextPath() %>/flightList">Liste</a></li>
            </ul>
        </div>
        
        
        <div class="menu-item">
            <a href="<%= request.getContextPath() %>/settings">
                <i class="fa-solid fa-gear menu-icon"></i>
                <span class="menu-text">Parametres</span>
            </a>
        </div>
    </div>
</div>

<script>
    // JavaScript pour le fonctionnement du menu déroulant
    document.addEventListener('DOMContentLoaded', function() {
        const menuItems = document.querySelectorAll('.has-submenu');
        
        menuItems.forEach(item => {
            item.querySelector('a').addEventListener('click', function(e) {
            e.preventDefault(); // empêche uniquement le lien du menu principal
            item.classList.toggle('open');
            const submenu = item.querySelector('.submenu');
            submenu.classList.toggle('open');
        });

        });
    });
</script>