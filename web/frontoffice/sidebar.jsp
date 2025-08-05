<div class="sidebar">     
    <div class="sidebar-header">         
        <h3><i class="fa-solid fa-plane sidebar-logo"></i> Air Manager</h3>     
    </div>          
    <div class="sidebar-menu">                  
        <div class="menu-item"> 
            <a href="<%= request.getContextPath() %>/user-vol">                 
                <i class="fa-solid fa-plane-departure menu-icon"></i>                 
                <span class="menu-text">Liste Vols</span>             
            </a> 
        </div>

    </div> 
</div>  

<style>
    /* Style pour cacher les sous-menus par défaut */
    .submenu {
        display: none;
    }
    
    /* Style pour afficher les sous-menus lorsqu'ils sont ouverts */
    .submenu.open {
        display: block;
    }
</style>

<script>     
    // JavaScript pour le fonctionnement du menu déroulant     
    document.addEventListener('DOMContentLoaded', function() {         
        const menuItems = document.querySelectorAll('.has-submenu');                  
        
        menuItems.forEach(item => {             
            item.querySelector('a').addEventListener('click', function(e) {             
                e.preventDefault(); // empêche le comportement par défaut du lien
                
                // Ferme tous les autres sous-menus
                menuItems.forEach(otherItem => {
                    if (otherItem !== item && otherItem.querySelector('.submenu.open')) {
                        otherItem.classList.remove('open');
                        otherItem.querySelector('.submenu').classList.remove('open');
                    }
                });
                
                // Ouvre/ferme le sous-menu actuel
                item.classList.toggle('open');             
                item.querySelector('.submenu').classList.toggle('open');             
            });          
        });     
    }); 
</script>