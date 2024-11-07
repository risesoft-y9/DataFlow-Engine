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
                    url: 'http://localhost:8091/api/restful/post/1',
                    header: [
                        {
                            isSelect: true,
                            Key: 'Access-Control-Request-Method',
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
                                headerKey: '',
                                headerParam: ''
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
                    url: 'http://localhost:8091/api/restful/post/2',
                    header: [
                        {
                            isSelect: true,
                            Key: 'Access-Control-Request-Method',
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
                                headerKey: '',
                                headerParam: ''
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
                    url: 'http://localhost:8091/api/restful/post/1',
                    header: [
                        {
                            isSelect: true,
                            Key: 'Access-Control-Request-Method',
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
                                headerKey: '',
                                headerParam: ''
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
