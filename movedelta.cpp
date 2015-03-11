#include "cubepos.h"
#include <iostream>
#include <stdlib.h>
#include <cstdio>
using namespace std ;
int md_lookup[] = { 0, 2, 2, 2, 1, 2, 2, 2, 2, 3, 3, 3, 3 } ;
int mindelta(const cubepos &cp, const cubepos &cp2 = identity_cube) {
   int s = 0 ;
   for (int i=0; i<12; i++)
      if (cp.e[i] != cp2.e[i])
         s++ ;
   int r = md_lookup[s] ;
   s = 0 ;
   for (int i=0; i<8; i++)
      if (cp.c[i] != cp2.c[i])
         s++ ;
   s = md_lookup[s] ;
   if (s > r)
      return s ;
   else
      return r ;
}
moveseq parsemoveseq(const char *s) {
  moveseq r ;
  const char *faces = "UuFfRrDdBbLlIiJjKk" ;
  while (*s) {
    while (*s && *s <= ' ')
       s++ ;
    if (*s == 0 || *s == '/')
       break ;
    const char *fp = strchr(faces, *s) ;
    if (fp == 0) {
       cerr << "Illegal face in " << *s << endl ;
       exit(10) ;
    }
    int f = (fp - faces) / 2 ;
    if (f * TWISTS > NMOVES)
       error("! bad face for this metric") ;
    s++ ;
    switch (*s) {
case '-':
case '3':
case '\'':
       r.push_back(f*TWISTS+2) ;
       s++ ;
       break ;
case '2':
       r.push_back(f*TWISTS+1) ;
       s++ ;
       break ;
case '+':
case '1':
       s++ ;
default:
       r.push_back(f*TWISTS) ;
       break ;
    }
  }
  return r ;
}
void parseposition(cubepos &cp, const char *s) {
  cp = identity_cube ;
  if (cp.parse_Singmaster(s) == 0) {
    cubepos cp2 ;
    cp.invert_into(cp2) ;
    cp = cp2 ;
    return ;
  }
  cp = identity_cube ;
  moveseq r = parsemoveseq(s) ;
  for (int i=0; i<r.size(); i++)
    cp.movepc(r[i]) ;
}
char buf[1001] ;
moveseq scramble ;
vector<cubepos> scramble_pos ;
cubepos loaded ;
int solveit(const cubepos &cp, int at, moveseq &r, int d) {
   cubepos cp2 ;
   if (d > 0) {
      for (int mv=0; mv<NMOVES; mv++) {
         cp2 = cp ;
         cp2.movepc(mv) ;
         int md = mindelta(cp2, scramble_pos[at]) ;
         if (md < d) {
            r.push_back(mv) ;
            if (solveit(cp2, at, r, d-1))
               return 1 ;
            r.pop_back() ;
         }
      }
   }
   if (at < scramble.size()) {
      cp2 = cp ;
      r.push_back(scramble[at]) ;
      cp2.movepc(scramble[at]) ;
      if (solveit(cp2, at+1, r, d))
         return 1 ;
      r.pop_back() ;
      return 0 ;
   }
   if (cp == identity_cube)
      return 1 ;
   else
      return 0 ;
}
int consider_rotations = 0 ;
int invert_position = 0 ;
int scramble_parsed = 0 ;
int position_parsed = 0 ;
int max_errors = -1 ;
vector<moveseq> scrambleset ;
void loadfile(const char *fn) {
   FILE *f = fopen(fn, "r") ;
   if (f == 0)
      error("! could not open scramble set file") ;
   while (fgets(buf, 1000, f) != 0) {
      scramble = parsemoveseq(buf);
      scrambleset.push_back(scramble) ;
   }
   fclose(f) ;
   cout << "Parsed " << scrambleset.size() << " scrambles." << endl ;
}
void setup_scramble(moveseq &ms) {
   scramble_pos.clear() ;
   scramble = ms ;
   cubepos cp, cp2 ;
   cp = identity_cube ;
   scramble_pos.push_back(cp) ;
   for (int i=0; i<scramble.size(); i++) {
      cp.movepc(cubepos::inv_move[scramble[scramble.size()-1-i]]) ;
      scramble_pos.push_back(cp) ;
   }
   reverse(scramble_pos.begin(), scramble_pos.end()) ;
}
int main(int argc, char *argv[]) {
   while (argc > 1 && argv[1][0] == '-') {
      argc-- ;
      argv++ ;
      switch (argv[0][1]) {
case 'm':
         consider_rotations++ ;
         break ;
case 'i':
         invert_position++ ;
         break ;
case 's':
         scramble = parsemoveseq(argv[0] + 2) ;
         scramble_parsed = 1 ;
         break;
case 'p':
         parseposition(loaded, argv[0] + 2) ;
         position_parsed = 1 ;
         break;
case 'e':
         max_errors = atoi (argv[0] + 2) ;
         break;
case 'f':
         loadfile(argv[0] + 2) ;
         scramble_parsed = 1 ;
         break ;
      }
   }
   if (scramble_parsed == 0) {
     if (fgets(buf, 1000, stdin) == 0)
      error("! need a scramble");
     scramble = parsemoveseq(buf);
   }
   if (position_parsed == 0) {
     if (fgets(buf, 1000, stdin) == 0)
        error("! need a position");
     parseposition(loaded, buf);
   }
   cubepos cp, cp2 ;
   moveseq r ;
   duration() ;
   if (scrambleset.size() == 0)
      scrambleset.push_back(scramble) ;
   for (int d=0; max_errors == -1 || d <= max_errors; d++) {
      cout << "Trying " << d << " mistakes " << flush ;
      for (int j=0; j<scrambleset.size(); j++) {
         setup_scramble(scrambleset[j]) ;
         for (int m=0; m<M && (m < 1 || consider_rotations); m++) {
            if (cubepos::move_map[m][0] % TWISTS != 0)
               continue ; // ignore mirror remaps
            if (invert_position)
               cp2 = loaded ;
            else
               loaded.invert_into(cp2) ;
            cp2.remap_into(m, cp) ;
            if (solveit(cp, 0, r, d)) {
               cout << endl << "Found a solution against " << j << endl ;
               if (m != 0)
                  cout << "Reoriented at " << m << endl ;
               cout << cubepos::moveseq_string(r) ;
               cout << endl ; 
               exit(0) ;
            }
         }
      }
      cout << "in " << duration() << endl ;
   }
}
