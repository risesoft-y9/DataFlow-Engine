/**
 * 获取接口数据
 * 数据格式要求，参考下面的 initData，注意id的格式要求
 */
export async function getTreeData() {
    fetch('http://localhost:3000/test').then((res) => {
        res.json().then((data) => {
            console.log(data);
        });
    });
}

/**
 * 保存接口数据
 * 后端自定义是一条一条保存还是整份全部保存，这里只做示例
 * 建议直接保存全部数据，减少繁琐工作，主要考虑该工具的可移植性
 */
export async function saveTreeData(item) {
    // 数据格式要求，参考下面的 initData
}
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
