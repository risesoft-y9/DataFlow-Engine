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
        name: '文件夹-1',
        type: 'folder',
        id: '1',
        children: [
            {
                name: '示例接口-1',
                id: '1-1',
                type: 'api',
                ApiForm: {
                    name: '示例接口-1',
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
                            Key: 'content-type',
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
                name: '示例接口-2',
                id: '1-2',
                type: 'api',
                ApiForm: {
                    name: '示例接口-2',
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
                            Key: 'content-type',
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
                name: '示例接口-3',
                id: '1-3',
                type: 'api',
                ApiForm: {
                    name: '示例接口-3',
                    method: 'DELETE',
                    url: 'http://localhost:3000/test',
                    header: [
                        {
                            isSelect: true,
                            Key: 'Connection',
                            Param: 'keep-alive'
                        },
                        {
                            isSelect: true,
                            Key: 'content-type',
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
            }
        ]
    }
];
