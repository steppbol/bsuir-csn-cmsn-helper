#include <stdio.h>

#include <stdlib.h>

bool check_braces(char *);

enum Braces
{
	round_brace_open = '(', 
	round_brace_closed = ')',
	figure_brace_open = '{',
	figure_brace_closed = '}',
	square_brace_open = '[',
	square_brace_closed = ']'
};

int main(int argc, char * argv[]) {
	if (argc != 3) {
		printf("ERROR: invalid file input/output or too many args\n");
		system("pause");
		return 0;
	}
	FILE * input_file;
	input_file = fopen(argv[1], "r");

	FILE * output_file;
	output_file = fopen(argv[2], "w");

	char source[100];

	while (!feof(input_file))
	{
		fscanf(input_file, "%s", source);
		if (check_braces(source)) {
			//printf("True");
			fprintf(output_file, "True\n");
		}
		else {
			//printf("False");
			fprintf(output_file, "False\n");
		}
	}

	fclose(input_file);
	fclose(output_file);

	return 0;
}



bool check_braces(char* source) {

	int round_brace_checker = 0, square_brace_checker = 0, figured_brace_checker = 0;

	while (*source)
	{
		switch (*source)
		{
		case round_brace_open:
			round_brace_checker++;
			break;
		case round_brace_closed:
			round_brace_checker--;
			break;
		case square_brace_open:
			square_brace_checker++;
			break;
		case square_brace_closed:
			square_brace_checker--;
			break;
		case figure_brace_open:
			figured_brace_checker++;
			break;
		case figure_brace_closed:
			figured_brace_checker--;					
		}
				
		if (round_brace_checker < 0 || square_brace_checker < 0 || figured_brace_checker < 0)
			return false;
		source++;
	}
	if (round_brace_checker == 0 && square_brace_checker == 0 && figured_brace_checker == 0)
		return true;
	else
		return false;
}