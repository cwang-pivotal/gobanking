import {dispatch} from 'redux'

import fetcher from '../fetcher'

export const [GET_PRODUCTS, SET_PRODUCTS] = ['GET_PRODUCTS', 'SET_PRODUCTS']

const setProducts = (products) => {
    return {
        type: SET_PRODUCTS,
        products
    }
}

const getProducts = () => {
    return (dispatch) => {
        fetcher.get('products').then((response) => {
            dispatch(setProducts(response.body))
        })
    }
}

const productActions = {
    getProducts
}

export default productActions