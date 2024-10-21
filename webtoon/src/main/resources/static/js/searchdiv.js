document.getElementById('search-button').addEventListener('click', function(event) {
    event.preventDefault();
    var searchContainer = document.getElementById('search-container');
    if (searchContainer.style.display === 'none') {
        searchContainer.style.display = 'block';
        searchContainer.classList.remove('fixed-top');
    } else {
        searchContainer.style.display = 'none';
    }
});

document.addEventListener('click', function(event) {
    var searchContainer = document.getElementById('search-container');
    var searchButton = document.getElementById('search-button');
    if (searchContainer.style.display === 'block' && !searchContainer.contains(event.target) && event.target !== searchButton) {
        searchContainer.style.display = 'none';
    }
});

document.getElementById('close-search').addEventListener('click', function() {
    document.getElementById('search-container').style.display = 'none';
});

window.addEventListener('scroll', function() {
    var navbar = document.querySelector('.navbar');
    var searchContainer = document.getElementById('search-container');
    if (window.scrollY > navbar.offsetHeight) {
        searchContainer.style.position = 'fixed';
        searchContainer.style.top = '20px';
    } else {
        searchContainer.style.position = 'absolute';
        searchContainer.style.top = '100px';
    }
});