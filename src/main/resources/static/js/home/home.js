(function () {
    var app = angular.module('homeApp', ['ngCookies', 'cartModule']);
    app.controller('homeCtrl', function ($scope, $http, $timeout, $cookies, CartService) {
        $scope.listProductsByCategory = [];
        $scope.listProductsNewest = [];
        $scope.listProductsMostPurchased = [];
        $scope.listCategories = [];
        $scope.selectedCategoryId = 6;
        $scope.user = {};

        var host = "http://localhost:8080/home/index";
        var urlImg = "http://localhost:8080/uploads/images";

        $scope.loadUserDetails = function () {
            $http.get('http://localhost:8080/auth/details').then(response => {
                if (response.status !== 200) {
                    console.log("⛔ User chưa đăng nhập hoặc không có dữ liệu.");
                    $scope.user = null;
                } else {
                    $scope.user = response.data;
                    console.log("✅ User details:", $scope.user);

                }
                $scope.loadAllCarts();
                console.log("User details:", $scope.user);
            }).catch(error => {
                console.log("Lỗi khi load chi tiết:", error);
            });
        };

        $scope.isAuthenticated = function () {
            console.log('Authorities:', $scope.user?.authorities); // Check what authorities array looks like
            if ($scope.user && Array.isArray($scope.user.authorities)) {
                return $scope.user.authorities.some(auth =>
                    auth.authority === 'ROLE_STAFF' || auth.authority === 'ROLE_DIRECTOR'
                );
            }
            return false;
        };

        $scope.loadAllProducts = function () {
            $http.get(`${host}/products/newest`).then(response => {
                $scope.listProductsNewest = response.data;
                $scope.listProductsNewest.forEach(p => {
                    if (p.image) {
                        p.image = `${urlImg}/${p.image}`;
                    }
                });
                console.log('listProductsNewest:',response.data);
            }).catch(error => {
                console.log(error);
            })
            $http.get(`${host}/products/mostpurchased`).then(response => {
                $scope.listProductsMostPurchased = response.data;
                $scope.listProductsMostPurchased.forEach(p => {
                    if (p.image) {
                        p.image = `${urlImg}/${p.image}`;
                    }
                });
                console.log('listProductsMostPurchased:',response.data);
            }).catch(error => {
                console.log(error);
            })
        }
        $scope.updateProductByCategory = function (categoryId) {
            $http.get(`${host}/products/category/${categoryId}`).then(response => {
                $scope.selectedCategoryId = categoryId;
                $scope.listProductsByCategory = response.data;
                $scope.listProductsByCategory.forEach(p => {
                    if (p.image) {
                        p.image = `${urlImg}/${p.image}`;
                    }
                });
                console.log('listProductsByCategory:',response.data);
            }).catch(error => {
                console.log(error);
            })
        }
        $scope.loadAllCategories = function (){
            $http.get(`${host}/categories`).then(response => {
                $scope.listCategories = response.data;
                console.log(response.data);
            }).catch(error => {
                console.log(error);
            });
        }
        $scope.addToCart = function (product) {
            CartService.addToCart(product, 1, $scope.user)
                .then(result => {
                    Swal.fire({
                        icon: 'success',
                        title: product.name + ' đã được thêm vào giỏ hàng!',
                        showConfirmButton: false,
                        timer: 1000
                    });
                    console.log("Kết quả thêm giỏ:", result);
                })
                .catch(error => {
                    console.error("Fail khi thêm giỏ:", error);
                    Swal.fire({
                        icon: 'error',
                        title: 'Thêm mới thất bại!',
                        text: 'Đã có lỗi xảy ra. Vui lòng thử lại.'
                    });
                });
        };
        $scope.goToDetail = function(productId) {
            window.location.href = `/shop/detail/${productId}`;
        };
        $scope.loadAllCategories();
        $scope.loadAllProducts();
        $scope.loadUserDetails();
        $scope.updateProductByCategory($scope.selectedCategoryId);

        $scope.formatPrice = function(price) {
            return price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
        };

        // category carousel
        $timeout(function () {
            $('.category-carousel').owlCarousel({
                loop: false,
                margin: 10,
                nav: true,
                navText: [
                    '<i class="bi bi-arrow-left"></i>',
                    '<i class="bi bi-arrow-right"></i>'
                ],
                dots: false,
                responsive: {
                    0: { items: 2 },
                    576: { items: 3 },
                    768: { items: 4 },
                    992: { items: 5 }
                }
            });
        }, 300); // Delay để đợi Angular render
    })
})();
$(document).ready(function () {
    setTimeout(function () {
        $(".vegetable-carousel").trigger('destroy.owl.carousel'); // clean old
        $(".vegetable-carousel").owlCarousel({
            autoplay: true,
            smartSpeed: 1500,
            center: false,
            dots: true,
            loop: true,
            margin: 25,
            nav: true,
            navText: [
                '<i class="bi bi-arrow-left"></i>',
                '<i class="bi bi-arrow-right"></i>'
            ],
            responsiveClass: true,
            responsive: {
                0: { items: 1 },
                576: { items: 1 },
                768: { items: 2 },
                992: { items: 3 },
                1200: { items: 4 }
            }
        });
    }, 300);
});
