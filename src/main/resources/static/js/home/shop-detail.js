(function () {
    var app = angular.module('shopDetailApp', ['ngCookies', 'cartModule']);
    app.controller('shopDetailController', function ($scope, $http, $cookies, CartService) {
        $scope.listProducts = [];
        $scope.listCategories = [];
        $scope.product = {};
        $scope.user = {};

        var host = "http://localhost:8080/home/index";
        var urlImg = "http://localhost:8080/uploads/images";

        const path = window.location.pathname;
        const id = path.split('/').pop();

        $scope.product = null;
        if (id) {
            $http.get(`${host}/shop/${id}`).then(response => {
                let product = response.data;
                if (product.image) {
                    product.image = `${urlImg}/${product.image}`;
                }
                $scope.product = product;
                console.log(product);
            }).catch(error => {
                console.log("Lỗi khi load chi tiết:", error);
            });
        }

        $scope.loadUserDetails = function () {
            $http.get('http://localhost:8080/auth/details').then(response => {
                if (response.status !== 200) {
                    console.log("⛔ User chưa đăng nhập hoặc không có dữ liệu.");
                    $scope.user = null;
                } else {
                    $scope.user = response.data;
                    console.log("✅ User details:", $scope.user);

                }
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

        $scope.loadAllCategories = function (){
            $http.get(`${host}/categories`).then(response => {
                $scope.listCategories = response.data;
                console.log(response.data);
            }).catch(error => {
                console.log(error);
            });
        }
        $scope.addToCart = function (product) {
            console.log(product);
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

        $scope.quantity = 1;
        $scope.increase = function() {
            if ($scope.quantity < 99) { // Giới hạn số lượng
                $scope.quantity++;
            }
        };

        // Hàm giảm số lượng
        $scope.decrease = function() {
            if ($scope.quantity > 1) { // Giới hạn số lượng không nhỏ hơn 1
                $scope.quantity--;
            }
        };

        $scope.loadAllCategories();
        $scope.loadUserDetails();

        $scope.formatPrice = function(price) {
            return price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
        };

    })
})();
