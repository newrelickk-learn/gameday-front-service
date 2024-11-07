import axios from "axios";
import {Config} from "./config";

const get = async (path: string, options?: any): Promise<any> => {
    // @ts-ignore
    const url = Config.URL + path
    const headers = {
        'Accept': 'application/json',
        'Content-type': 'application/json',
    }
    try {
        const response = await axios.get(url, {...options, headers,  withCredentials: true})
        if (response.status === 401) {
            console.error(response)
        }
        return response.data
    } catch (error: any) {
        const err = new Error(`GET ${url} is faled`)
        throw err
    }
}

const post = async (path: string, object?: any, options?: any, needRedirect: boolean = true): Promise<any> => {
    // @ts-ignore
    const url = Config.URL + path
    const _object = { ...object }
    const headers = {
        'Accept': 'application/json',
        'Content-type': 'application/json',
    }

    if (!_object.username) {
        //_object.token = localStorage.getItem('token')
    }
    try {
        const response = await axios.post(url, _object, {...options, headers, withCredentials: true})
        if (response.status === 401) {
            console.error(response)
        }
        return response.data
    } catch (e: any) {
        console.error(e)

        return undefined;
    }
}

export const API = {
    get, post
}