import requests

# 调用测试方法获取所有团队成员
response = requests.get('http://localhost:8080/teams/members/all')

if response.status_code == 200:
    data = response.json()
    print('获取团队成员成功')
    print('状态码:', data.get('code'))
    print('消息:', data.get('msg'))
    print('数据:')
    for member in data.get('data', []):
        print(f"团队成员ID: {member.get('id')}, 团队ID: {member.get('teamId')}, 用户ID: {member.get('userId')}, 角色: {member.get('roleInTeam')}")
else:
    print('获取团队成员失败')
    print('状态码:', response.status_code)
    print('响应内容:', response.text)
