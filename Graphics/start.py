import pygame

pygame.init()

BLACK    = (   0,   0,   0)
WHITE    = ( 255, 255, 255)
GREEN    = (   0, 255,   0)
RED      = ( 255,   0,   0)
BLUE     = (   0,   0, 255)

size = (1000, 500)
screen = pygame.display.set_mode(size)

ok = True
clock = pygame.time.Clock()
while ok:
	file = open("laby",'r');

	c = 0
	list_players = []
	maze= []
	for i in file:
		if(i[0] == '$'):
			width_height = i
			continue
		
		if(i[0] == '#' and c == 0):
			c = 1
			continue
		

		if(c == 1):
			if(i[0] == '#'):
				c = 0
				continue
			list_players.append(i)
			continue
		maze.append(i.strip('\n'))


	

	w_h = width_height[1:].split(" ")
	width = int(w_h[1])
	height = int(w_h[0])
	#print(width)
	#print(height)

	#print(list_players)
	#print(maze)
	screen.fill(BLACK)

	#print(maze)
	x = 0
	y = 0
	
	wall = pygame.image.load("wall.jpeg").convert()
	ghost = pygame.image.load("ghost.jpg").convert()
	player = pygame.image.load("player.jpeg").convert()
	road = pygame.image.load("road.jpg").convert()
	wallpaper = pygame.image.load("wallpaper.jpeg").convert()


	font=pygame.font.Font(None,30)
	scoretext=font.render("                 programmed by Achraf && tuan           ", 1,(255,0,0))
	screen.blit(scoretext, (10, 0))

	#print(len(maze[0]))
	for i in range(0,len(maze)):
		for j in range(0,len(maze[i])):
			if(maze[i][j] == '0'): #wall
				#pygame.draw.rect(screen, RED, [j*20, i*20 + 20, 20, 20])
				screen.blit(wall,(j*20,i*20 + 20))
			if(maze[i][j] == '1'):#road
				#pygame.draw.rect(screen, BLUE, [j*20, i*20 + 20, 20, 20])
				screen.blit(road,(j*20,i*20 +20))
			if(maze[i][j] == '2'): #ghost
				#pygame.draw.rect(screen, GREEN, [j*20, i*20 + 20, 20, 20])
				screen.blit(ghost,(j*20,i*20 +20))
			if(maze[i][j] == '3'): #player
				#pygame.draw.rect(screen, GREEN, [j*20, i*20 + 20, 20, 20])
				screen.blit(player,(j*20,i*20 +20))


	pygame.draw.line(screen, GREEN, [0, 250], [1000, 250], 5)
	screen.blit(wallpaper,(700,260))
	c = 0
	for i in list_players:
		id_1,ip,port,score = i.strip('\n').split(":")
		p = "Player :"+id_1+" ip :"+ip+" port: "+port+" score :"+score
		font=pygame.font.Font(None,30)
		scoretext=font.render("Player : "+p, 1,(0,0,255))
		c += 1
		screen.blit(scoretext, (2, 250 + c*30))

	pygame.display.flip()

	clock.tick(60)