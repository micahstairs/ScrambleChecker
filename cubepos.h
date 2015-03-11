/*2:*/
#line 19 "./cubepos.w"

#ifndef CUBEPOS_H
#define CUBEPOS_H
#include <cstring> 
#include <cstdlib> 
#include <stddef.h> 
#include <vector> 
#include <algorithm> 
#ifdef _WIN32
#include <windows.h> 
#include <intrin.h> 
inline int ffs1(int v){unsigned long r;_BitScanForward(&r,v);return(int)r;}
#else
inline int ffs1(int v){return ffs(v)-1;}
#include <sys/time.h> 
#endif
using namespace std;

/*:2*//*3:*/
#line 57 "./cubepos.w"

#ifdef HALF
#ifdef SLICE
#error "Can't define HALF and SLICE"
#endif
#ifdef QUARTER
#error "Can't define HALF and SLICE"
#endif
#else
#ifdef SLICE
#ifdef QUARTER
#error "Can't define SLICE and QUARTER"
#endif
#else
#ifndef QUARTER
#error "Please define one of HALF, SLICE, or QUARTER"
#endif
#endif
#endif

/*:3*//*4:*/
#line 83 "./cubepos.w"

#ifdef HALF
const int NMOVES= 18;
const int TWISTS= 3;
#endif
#ifdef QUARTER
const int NMOVES= 12;
const int TWISTS= 2;
#endif
#ifdef SLICE
const int NMOVES= 27;
const int TWISTS= 3;
#endif
const int FACES= 6;
const int M= 48;
const int CUBIES= 24;

/*:4*//*5:*/
#line 113 "./cubepos.w"

extern const class cubepos identity_cube;
/*21:*/
#line 478 "./cubepos.w"

#ifdef QUARTER
const int NMOVES_EXT= NMOVES+4;
#else
const int NMOVES_EXT= NMOVES;
#endif

/*:21*//*32:*/
#line 749 "./cubepos.w"

typedef vector<int> moveseq;

/*:32*//*67:*/
#line 1566 "./cubepos.w"

const int ALLMOVEMASK= (1<<NMOVES)-1;
const int ALLMOVEMASK_EXT= (1<<NMOVES_EXT)-1;

/*:67*//*73:*/
#line 1709 "./cubepos.w"

#ifdef HALF
const int CANONSEQSTATES= FACES+1;
#endif
#ifdef QUARTER
const int CANONSEQSTATES= 2*FACES+1;
#endif
#ifdef SLICE
const int CANONSEQSTATES= 5*FACES/2+1;
#endif
const int CANONSEQSTART= 0;

/*:73*//*78:*/
#line 1825 "./cubepos.w"

void error(const char*s);
double myrand();
inline int random_move(){return(int)(NMOVES*myrand());}
inline int random_move_ext(){return(int)(NMOVES_EXT*myrand());}
double walltime();
double duration();

/*:78*//*80:*/
#line 1884 "./cubepos.w"

void init_mutex();
void get_global_lock();
void release_global_lock();
#ifdef THREADS
#ifdef _WIN32
#include <windows.h> 
#include <process.h> 
#define THREAD_RETURN_TYPE unsigned int
#define THREAD_DECLARATOR __stdcall
#else
#include <pthread.h> 
#define THREAD_RETURN_TYPE void *
#define THREAD_DECLARATOR
#endif
const int MAX_THREADS= 128;
void spawn_thread(int i,THREAD_RETURN_TYPE(THREAD_DECLARATOR*p)(void*),
void*o);
void join_thread(int i);
#else
#define THREAD_RETURN_TYPE void *
#define THREAD_DECLARATOR
const int MAX_THREADS= 1;
#endif

/*:80*/
#line 115 "./cubepos.w"

class cubepos{
public:
/*9:*/
#line 256 "./cubepos.w"

inline bool operator<(const cubepos&cp)const{
return memcmp(this,&cp,sizeof(cp))<0;
}
inline bool operator==(const cubepos&cp)const{
return memcmp(this,&cp,sizeof(cp))==0;
}
inline bool operator!=(const cubepos&cp)const{
return memcmp(this,&cp,sizeof(cp))!=0;
}

/*:9*//*10:*/
#line 275 "./cubepos.w"

static inline int edge_perm(int cubieval){return cubieval>>1;}
static inline int edge_ori(int cubieval){return cubieval&1;}
static inline int corner_perm(int cubieval){return cubieval&7;}
static inline int corner_ori(int cubieval){return cubieval>>3;}
static inline int edge_flip(int cubieval){return cubieval^1;}
static inline int edge_val(int perm,int ori){return perm*2+ori;}
static inline int corner_val(int perm,int ori){return ori*8+perm;}
static inline int edge_ori_add(int cv1,int cv2){return cv1^edge_ori(cv2);}
static inline int corner_ori_add(int cv1,int cv2)\
{return mod24[cv1+(cv2&0x18)];}
static inline int corner_ori_sub(int cv1,int cv2)\
{return cv1+corner_ori_neg_strip[cv2];}
static void init();

/*:10*//*15:*/
#line 367 "./cubepos.w"

inline cubepos(const cubepos&cp= identity_cube){*this= cp;}
cubepos(int,int,int);

/*:15*//*20:*/
#line 468 "./cubepos.w"

void move(int mov);

/*:20*//*33:*/
#line 758 "./cubepos.w"

static int invert_move(int mv){return inv_move[mv];}
static moveseq invert_sequence(const moveseq&sequence);
void invert_into(cubepos&dst)const;

/*:33*//*39:*/
#line 873 "./cubepos.w"

void movepc(int mov);

/*:39*//*43:*/
#line 1005 "./cubepos.w"

static void mul(const cubepos&a,const cubepos&b,cubepos&r);
inline static void mulpc(const cubepos&a,const cubepos&b,cubepos&r){
mul(b,a,r);
}

/*:43*//*45:*/
#line 1033 "./cubepos.w"

static void skip_whitespace(const char*&p);
static int parse_face(const char*&p);
static int parse_face(char f);
#ifdef SLICE
static int parse_moveface(const char*&p);
static int parse_moveface(char f);
static void append_moveface(char*&p,int f){*p++= movefaces[f];}
static void append_face(char*&p,int f){*p++= movefaces[f];}
#else
static void append_face(char*&p,int f){*p++= faces[f];}
#endif
static int parse_move(const char*&p);
static void append_move(char*&p,int mv);
static moveseq parse_moveseq(const char*&p);
static void append_moveseq(char*&p,const moveseq&seq);
static char*moveseq_string(const moveseq&seq);

/*:45*//*55:*/
#line 1296 "./cubepos.w"

const char*parse_Singmaster(const char*p);
char*Singmaster_string()const;

/*:55*//*66:*/
#line 1558 "./cubepos.w"

void remap_into(int m,cubepos&dst)const;
void canon_into48(cubepos&dst)const;
void canon_into48_aux(cubepos&dst)const;
void canon_into96(cubepos&dst)const;

/*:66*//*70:*/
#line 1629 "./cubepos.w"

void randomize();

/*:70*//*77:*/
#line 1816 "./cubepos.w"

static inline int next_cs(int cs,int mv){return canon_seq[cs][mv];}
static inline int cs_mask(int cs){return canon_seq_mask[cs];}
static inline int cs_mask_ext(int cs){return canon_seq_mask_ext[cs];}

/*:77*/
#line 118 "./cubepos.w"

/*12:*/
#line 332 "./cubepos.w"

static unsigned char corner_ori_inc[CUBIES],corner_ori_dec[CUBIES],
corner_ori_neg_strip[CUBIES],mod24[2*CUBIES];

/*:12*//*18:*/
#line 424 "./cubepos.w"

static char faces[FACES];
#ifdef SLICE
static char movefaces[FACES+3];
#endif

/*:18*//*22:*/
#line 495 "./cubepos.w"

static unsigned char edge_trans[NMOVES_EXT][CUBIES],
corner_trans[NMOVES_EXT][CUBIES];

/*:22*//*34:*/
#line 765 "./cubepos.w"

static unsigned char inv_move[NMOVES_EXT];

/*:34*//*58:*/
#line 1413 "./cubepos.w"

static unsigned char face_map[M][FACES],move_map[M][NMOVES_EXT];
static unsigned char invm[M],mm[M][M];
static unsigned char rot_edge[M][CUBIES],rot_corner[M][CUBIES];

/*:58*//*74:*/
#line 1724 "./cubepos.w"

static unsigned char canon_seq[CANONSEQSTATES][NMOVES_EXT];
static int canon_seq_mask[CANONSEQSTATES];
static int canon_seq_mask_ext[CANONSEQSTATES];

/*:74*/
#line 119 "./cubepos.w"

/*7:*/
#line 192 "./cubepos.w"

unsigned char c[8];

/*:7*//*8:*/
#line 249 "./cubepos.w"

unsigned char e[12];

/*:8*/
#line 120 "./cubepos.w"

};
/*16:*/
#line 378 "./cubepos.w"

static cubepos cubepos_initialization_hack(1,2,3);

/*:16*/
#line 122 "./cubepos.w"

#endif

/*:5*/
