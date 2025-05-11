(function () {
    var app = angular.module('userProfileApp', ['ngCookies']);
    app.controller('userProfileCtrl', function ($scope, $http, $cookies) {
        $scope.listOrders = [];
        $scope.user = {};
        $scope.newPassword = '';
        $scope.currentPassword = '';
        $scope.orderShow = {};

        var host = "http://localhost:8080/auth";
        // var urlImg = "http://localhost:8080/uploads/images";

        $http.get(`${host}/details`).then(response => {
            $scope.user = response.data;

            $scope.currentPassword = $scope.user.password;
            console.log('userDetails', $scope.user);
        }).catch(error => {
            console.log("Lỗi khi load chi tiết:", error);
        });
        $scope.isAuthenticated = function () {
            console.log('Authorities:', $scope.user?.authorities); // Check what authorities array looks like
            if ($scope.user && Array.isArray($scope.user.authorities)) {
                return $scope.user.authorities.some(auth =>
                    auth.authority === 'ROLE_STAFF' || auth.authority === 'ROLE_DIRECTOR'
                );
            }
            return false;
        };
        $scope.updateProfile = function () {
            var item = angular.copy($scope.user);
            var url = `${host}/update`;

            if ($scope.newPassword !== '' || $scope.newPassword.trim().length > 0) {
                item.password = $scope.newPassword;
            }else{
                item.password = $scope.currentPassword;
            }
            console.log(item);
            $http.put(url, item).then(response => {
                $scope.user = response.data;
                console.log(response.data);
                Swal.fire({
                    icon: 'success',
                    title: 'Cập nhật thành công!',
                    showConfirmButton: false,
                    timer: 1000
                });
            }).catch(error => {
                console.log(error);
                Swal.fire({
                    icon: 'error',
                    title: 'Cập nhật thất bại!',
                    text: 'Đã có lỗi xảy ra. Vui lòng thử lại.'
                });
            })

        }

        $scope.loadAllOrders = function (){
            $http.get(`${host}/orders`).then(response => {
                $scope.listOrders = response.data;
                $scope.listOrders.forEach(order => {
                    order.total = $scope.calculateTotal(order.orderDetails);
                });
                console.log(response.data);
            }).catch(error => {
                console.log(error);
            });
        }

        $scope.calculateTotal = function(orderDetails) {
            if (!orderDetails || orderDetails.length === 0) return 0;
            return orderDetails.reduce((total, detail) => {
                return total + (detail.price * detail.quantity);
            }, 0);
        };

        $scope.showOrder = function (orderId) {
            $scope.orderShow = $scope.listOrders.find(order => order.id === orderId);

        }

        $scope.loadAllOrders();

        $scope.formatPrice = function(price) {
            return price.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
        };
        $scope.formatStatus = function(status) {
            switch (status) {
                case 'PENDING':
                    return 'Chờ xử lý';
                case 'PROCESSING':
                    return 'Đang xử lý';
                case 'SHIPPED':
                    return 'Đã giao hàng';
                case 'DELIVERED':
                    return 'Đã nhận hàng';
                case 'CANCELLED':
                    return 'Đã hủy';
                default:
                    return status;
            }
        };
    })
})();
