import Request from '@/api/lib/request';
import qs from 'qs';
const platformRequest = Request();

/**
 * 获取接口数据
 * 数据格式要求，参考下面的 initData，注意id的格式要求
 */
export const getTreeData = async () => {
    return await platformRequest({
        url: 'apionline/getTree',
        method: 'GET',
        cType: false
    });
};

// export async function getTreeData(id) {
//     fetch(import.meta.env.VUE_APP_CONTEXT + 'apionline/getTree?' + id).then((res) => {
//         res.json().then((data) => {
//             console.log(data);
//         });
//     });
// }

/**
 * 保存数据
 */
export const saveTreeData = async (data) => {
    return await platformRequest({
        url: 'apionline/saveData',
        method: 'POST',
        JSON: true,
        data
    });
};

/**
 * 删除节点
 * @param params
 * @returns
 */
export const removeNode = async (params) => {
    const data = qs.stringify(params);
    return await platformRequest({
        url: 'apionline/deleteData',
        method: 'POST',
        cType: false,
        data
    });
};

export const randomString = (e) => {
    var e = e || 32,
        t = 'ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789',
        a = t.length,
        n = '';
    for (let i = 0; i < e; i++) n += t.charAt(Math.floor(Math.random() * a));
    return n;
};

export const initData = [
    {
        name: '接口示例',
        type: 'folder',
        id: '1',
        children: [
            {
                name: '示例接口-1（get）',
                id: '1-1',
                type: 'api',
                ApiForm: {
                    name: '示例接口-1（get）',
                    method: 'GET',
                    url: 'http://localhost:3000/test',
                    header: [
                        {
                            isSelect: true,
                            Key: 'Connection',
                            Param: 'keep-alive'
                        },
                        {
                            isSelect: true,
                            Key: 'Content-Type',
                            Param: 'application/json'
                        }
                    ],
                    query: [
                        {
                            isSelect: false,
                            Key: '',
                            Param: ''
                        }
                    ],
                    body: {
                        type: 1,
                        1: null,
                        2: [
                            {
                                isSelect: false,
                                Key: '',
                                Param: ''
                            }
                        ],
                        3: null,
                        4: null,
                        5: null,
                        6: null,
                        7: null
                    }
                }
            },
            {
                name: '示例接口-2（post）',
                id: '1-2',
                type: 'api',
                ApiForm: {
                    name: '示例接口-2（post）',
                    method: 'POST',
                    url: 'http://localhost:3000/test',
                    header: [
                        {
                            isSelect: true,
                            Key: 'Connection',
                            Param: 'keep-alive'
                        },
                        {
                            isSelect: true,
                            Key: 'Content-Type',
                            Param: 'application/json'
                        }
                    ],
                    query: [
                        {
                            isSelect: false,
                            Key: '',
                            Param: ''
                        }
                    ],
                    body: {
                        type: 1,
                        1: null,
                        2: [
                            {
                                isSelect: false,
                                Key: '',
                                Param: ''
                            }
                        ],
                        3: null,
                        4: null,
                        5: null,
                        6: null,
                        7: null
                    }
                }
            },
            {
                name: '示例接口-3（post-formdata）',
                id: '1-3',
                type: 'api',
                ApiForm: {
                    name: '示例接口-3（post-formdata）',
                    method: 'POST',
                    url: 'http://localhost:3000/test/formdata',
                    header: [
                        {
                            isSelect: true,
                            Key: 'Connection',
                            Param: 'keep-alive'
                        },
                        {
                            isSelect: true,
                            Key: 'Content-Type',
                            Param: 'multipart/form-data'
                        }
                    ],
                    query: [
                        {
                            isSelect: false,
                            Key: '',
                            Param: ''
                        }
                    ],
                    body: {
                        type: 2,
                        1: null,
                        2: [
                            {
                                isSelect: false,
                                Key: '',
                                Param: ''
                            }
                        ],
                        3: null,
                        4: null,
                        5: null,
                        6: null,
                        7: null
                    }
                }
            },
            {
                name: '示例接口-4（files）',
                id: '1-4',
                type: 'api',
                ApiForm: {
                    name: '示例接口-4（files）',
                    method: 'POST',
                    url: 'http://localhost:3000/upload-files',
                    header: [
                        {
                            isSelect: true,
                            Key: 'Connection',
                            Param: 'keep-alive'
                        },
                        {
                            isSelect: true,
                            Key: 'Content-Type',
                            Param: 'multipart/form-data'
                        }
                    ],
                    query: [
                        {
                            isSelect: false,
                            Key: '',
                            Param: ''
                        }
                    ],
                    body: {
                        type: 7,
                        1: null,
                        2: [
                            {
                                isSelect: false,
                                Key: '',
                                Param: ''
                            }
                        ],
                        3: null,
                        4: null,
                        5: null,
                        6: null,
                        7: null
                    }
                }
            }
        ]
    }
    // {
    //     name: '接口示例',
    //     type: 'folder',
    //     id: '2',
    //     children: [
    //         {
    //             name: '示例接口-1（get）',
    //             id: '2-1',
    //             type: 'api',
    //             ApiForm: {
    //                 name: '示例接口-1（get）',
    //                 method: 'GET',
    //                 url: 'http://localhost:3000/test',
    //                 header: [
    //                     {
    //                         isSelect: true,
    //                         Key: 'Connection',
    //                         Param: 'keep-alive'
    //                     },
    //                     {
    //                         isSelect: true,
    //                         Key: 'Content-Type',
    //                         Param: 'application/json'
    //                     }
    //                 ],
    //                 query: [
    //                     {
    //                         isSelect: false,
    //                         Key: '',
    //                         Param: ''
    //                     }
    //                 ],
    //                 body: {
    //                     type: 1,
    //                     1: null,
    //                     2: [
    //                         {
    //                             isSelect: false,
    //                             Key: '',
    //                             Param: ''
    //                         }
    //                     ],
    //                     3: null,
    //                     4: null,
    //                     5: null,
    //                     6: null,
    //                     7: null
    //                 }
    //             }
    //         }
    //     ]
    // }
];
