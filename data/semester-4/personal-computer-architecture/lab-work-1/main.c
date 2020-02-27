// f(x) = |sin^2(x) * cos(x)|

#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <conio.h>
#include <time.h>

void main(void)
{
	double lower_border = -10000, upper_border = 10000, step = 0.001, x, function;
	int flag = 1;
	int two = 2;
	clock_t start, end;

	do
	{
		if (!flag)
			printf("%s", "Error while reading input data! Repeat the input to continue\n");

		printf("%s", "Please, input lower border value (a) :\n");

		rewind(stdin);
		flag = scanf_s("%lf", &lower_border);

		system("cls");
	} while (!flag); 

	do
	{
		if (!flag)
			printf("%s", "Error while reading input data! Repeat the input to continue\n");

		printf("%s", "Please, input upper border value (b) :\n");

		rewind(stdin);
		flag = scanf_s("%lf", &upper_border);

		system("cls");
	} while (!flag);

	if (upper_border < lower_border)
	{
		printf("Error! Unexcpected data were recived! You can not set upper border less than lower border");
		system("pause");
		exit(EXIT_FAILURE);
	}

	do
	{
		if (!flag)
			printf("%s", "Error while reading input data! Repeat the input to continue\n");

		printf("%s", "Please, input step value :\n");

		rewind(stdin);
		flag = scanf_s("%lf", &step);

		if (step <= 0)
			flag = 0;

		system("cls");
	} while (!flag);

	x = lower_border;
	function = 0;
	start = clock();

	_asm {
		finit			

		fld upper_border
						
		fld x

loop_start :
			fcom				//compare command
			fstsw ax			//copying state register value to AX
			and ah, 01000101b	//checking state register (bits 8, 10, 14 - C0, C2, C3) and muting all other bits
			jz loop_end			//if x == upper_border jump to loop_end

			fld x				//ST(0) = x
								//ST(1) = x
								//ST(1) = upper_border

			fsin				//calculating sin for ST(0)
								//ST(0) = sin(x)
								//ST(1) = x
								//ST(2) = upper_border

			fld ST(0)			//copy ST(0)
								//ST(0) = sin(x)
								//ST(1) = sin(x)
								//ST(2) = x
								//ST(3) = upper_border

			fmul				//multiplying ST(0) & ST(1)

			fld x				//loading x to register stack ST(0) = x
								//ST(0) = x
								//ST(1) = sin^2(x)
								//ST(2) = x
								//ST(3) = upper_border

			fcos				//calculating cos for ST(0)
								//ST(0) = cos(x)
								//ST(1) = sin^2(x)
								//ST(2) = x
								//ST(3) = upper_border

			fmul				//multiply ST(0) = ST(0) * ST(1);
								//ST(0) = (cos(x) * sin^2(x))
								//ST(1) = x
								//ST(2) = upper_border

			fabs				//absolute value of ST(0)
								//ST(0) = |cos(x) * sin^2(x)|
								//ST(1) = x
								//ST(2) = upper_border

			fadd function		//adding 
								//ST(0) = |cos(x) * sin^2(x)| + function
								//ST(1) = x
								//ST(2) = upper_border

			fstp function		//copying ST(0) into function and poping ST(0)

			fadd step			//adding
								//ST(0) = x + step
								//ST(1) = upper_border

			fst x				//copying ST(0) to x
								//ST(0) = x + step
								//ST(1) = upper_border

			jmp loop_start		//moving to loop_star without 'if'
			loop_end :
		fwait
	}

	end = clock();
	printf("Function value : %f\n", function);
	printf("Time ASM : %f\n", (double)(end - start) / CLK_TCK);

	x = lower_border;
	function = 0;

	start = clock();
	while (x <= upper_border)
	{
		function += fabs(pow(sin(x), 2) * cos(x));
		x += step;
	}
	end = clock();
	printf("\n\nFunction value : %f\n", function);
	printf("Time C : %f\n\n", (double)(end - start) / CLK_TCK);

	system("pause");
}


// F(x) = sin(x + 1) / (cos(x) + 2)
/*_asm {
finit			// initializing coprossesor

fld b			// loading real number into stack
//ST(0) = b
fld x			//ST(0) = x
//ST(1) = b
loop_start :
fcom				//compare command
fstsw ax			//copying state register value to AX
and ah, 01000101b	//checking state register (bits 8, 10, 14 - C0, C2, C3)
jz loop_end			//if x==b jump to loop_end

fld1				//loading const 1
//ST(0) = 1
//ST(1) = x
//ST(2) = b
fadd x				//adding ST(0) = ST(0) + x
//ST(0) = 1 + x
//ST(1) = x
//ST(2) = b
fsin				//calculating sin for ST(0)
//ST(0) = sin(1 + x)
//ST(1) = x
//ST(2) = b
fld x				//loading x to register stack ST(0) = x
//ST(0) = x
//ST(1) = sin(1 + x)
//ST(2) = x
//ST(3) = b
fcos				//calculating cos for ST(0)
//ST(0) = cos(x)
//ST(1) = sin(1 + x)
//ST(2) = x
//ST(3) = b
fiadd two			//adding ST(0) = ST(0) + two
//ST(0) = cos(x) + 2
//ST(1) = sin(1 + x)
//ST(2) = x
//ST(3) = b
fdiv				//dividing ST(0) = ST(0) / ST(1);
//ST(0) = (cos(x) + 2) / (sin(1 + x))
//ST(1) = x
//ST(2) = b
fadd f				//adding
//ST(0) = (cos(x) + 2) / (sin(1 + x)) + f
//ST(1) = x
//ST(2) = b
fstp f				//copying ST(0) into f and poping ST(0)

fadd step			//adding
//ST(0) = x + step
//ST(1) = b
fst x				//copying ST(0) to x
//ST(0) = x + step
//ST(1) = b
jmp loop_start		//moving to loop_star without 'if'
loop_end :
fwait
}*/

/*start = clock();
	while (x <= b)
	{
		f += sin(x + 1) / (cos(x) + 2);
		x += step;
	}
	end = clock();*/