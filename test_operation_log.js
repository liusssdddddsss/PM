const axios = require('axios');

async function testOperationLog() {
    try {
        const response = await axios.post('http://localhost:8080/workbench/operation-logs', {
            username: '202201',
            action: '测试操作',
            targetId: 1,
            targetType: 'test',
            createTime: new Date().toISOString()
        }, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
        console.log('Response:', response.data);
    } catch (error) {
        console.error('Error:', error.response ? error.response.data : error.message);
    }
}

testOperationLog();