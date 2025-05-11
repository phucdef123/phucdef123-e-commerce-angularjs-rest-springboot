angular.module('cartModule', []).factory('CartService', function ($http, $cookies) {
    const host = 'http://localhost:8080/cart';

    return {
        addToCart: function (product, quantity, user) {
            const item = {
                id: null,
                product: {
                    id: product.id,
                    name: product.name,
                    image: product.image,
                    price: product.price
                },
                quantity: quantity,
                user: {}
            };

            if (user) {
                item.user = user;

                // 🔍 Check nếu sản phẩm đã tồn tại trong giỏ hàng của user (trên server)
                return $http.get(`${host}/user/${user.username}`).then(response => {
                    const existingCart = response.data;
                    const found = existingCart.find(c => c.product.id === item.product.id);

                    if (found) {
                        // 🔄 Nếu đã có thì update số lượng
                        found.quantity += item.quantity;
                        return $http.put(`${host}/${found.id}`, found);
                    } else {
                        // ➕ Nếu chưa có thì thêm mới
                        return $http.post(host, item);
                    }
                });
            } else {
                // 🧠 Chưa đăng nhập thì xài cookie như cũ
                let cartArray = [];
                const existingCart = $cookies.get('cart');

                if (existingCart) {
                    cartArray = JSON.parse(existingCart);
                    const found = cartArray.find(c => c.product.id === item.product.id);
                    if (found) {
                        found.quantity += item.quantity;
                    } else {
                        cartArray.push(item);
                    }
                } else {
                    cartArray.push(item);
                }

                $cookies.put('cart', JSON.stringify(cartArray), {
                    path: '/',
                    expires: new Date(Date.now() + 3600 * 1000)
                });

                return Promise.resolve(cartArray);
            }
        }

    };
});
