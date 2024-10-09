document.addEventListener('DOMContentLoaded', () => {
    const carouselInner = document.getElementById('carousel-inner');
    const prevButton = document.getElementById('carousel-control-prev');
    const nextButton = document.getElementById('carousel-control-next');
    const indicatorsContainer = document.getElementById('carousel-indicators');
    let currentIndex = 0;
    let images = [];

    // Fetch images from the static folder
    fetch('/carousel/images')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(fetchedImages => {
            images = fetchedImages;
            images.forEach((imageSrc, index) => {
                const itemDiv = document.createElement('div');
                itemDiv.classList.add('carousel-item');
                itemDiv.style.width = '100%';

                if (index === 0) {
                    itemDiv.classList.add('active');
                }
                const imgElement = document.createElement('img');
                imgElement.src = `/static/carousel/${imageSrc}?v=${new Date().getTime()}`;
                imgElement.alt = `Carousel Image ${index + 1}`;
                imgElement.style.width = '100%';
                imgElement.style.height = '100%';
                itemDiv.appendChild(imgElement);
                carouselInner.appendChild(itemDiv);

                // Create indicator button
                const indicatorButton = document.createElement('button');
                if (index === 0) {
                    indicatorButton.classList.add('active');
                }
                indicatorButton.addEventListener('click', () => {
                    updateCarousel(index);
                    resetAutoSlide();
                });
                indicatorsContainer.appendChild(indicatorButton);
            });

            // Start automatic sliding
            autoSlideInterval = setInterval(() => {
                updateCarousel((currentIndex + 1) % images.length);
            }, 5000);
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });

    let autoSlideInterval;

    const resetAutoSlide = () => {
        clearInterval(autoSlideInterval);
        autoSlideInterval = setInterval(() => {
            updateCarousel((currentIndex + 1) % images.length);
        }, 5000);
    };

    const updateCarousel = (newIndex) => {
        const items = document.querySelectorAll('.carousel-item');
        const indicators = document.querySelectorAll('.carousel-indicators button');
        if (items.length === 0) return;

        carouselInner.style.transition = 'transform 1s ease-in-out';
        carouselInner.style.transform = `translateX(-${newIndex * 100}%)`;

        indicators[currentIndex].classList.remove('active');
        currentIndex = newIndex;
        indicators[currentIndex].classList.add('active');
    };

    prevButton.addEventListener('click', (e) => {
        e.preventDefault();
        updateCarousel((currentIndex - 1 + images.length) % images.length);
        resetAutoSlide();
    });

    nextButton.addEventListener('click', (e) => {
        e.preventDefault();
        updateCarousel((currentIndex + 1) % images.length);
        resetAutoSlide();
    });
});