__kernel  void OCL_SSSP_KERNEL1(__global const int *V, __global const int *E, __global const float *W,
                               __global int *M, __global int *P, __global float *C, __global float *U,
                               int vertexCount, int edgeCount )
{
    int tid = get_global_id(0);

    if ( M[tid] == 1 )
    {
        M[tid] = 0;

        int edgeStart = V[tid];
        int edgeEnd;
        if (tid + 1 < (vertexCount))
        {
            edgeEnd = V[tid + 1];
        }
        else
        {
            edgeEnd = edgeCount;
        }

        for(int edge = edgeStart; edge < edgeEnd; edge++)
        {
            int nid = E[edge];
            float val = (C[tid] + W[edge]);
            if (U[nid] > val)
            {
                U[nid] = val;
                P[nid] = tid;
            }
        }
    }
}

__kernel  void OCL_SSSP_KERNEL2(__global int *V, __global int *E, __global float *W,
                                __global int *M, __global float *C, __global float *U,
                                int vertexCount)
{
    // access thread id
    int tid = get_global_id(0);

    if (C[tid] > U[tid])
    {
        C[tid] = U[tid];
        M[tid] = 1;
    }
    U[tid] = C[tid];
}


__kernel  void OCL_SSSP_INIT(__global const int *V, __global const int *E, __global const float *W,
                               __global int *M, __global int *P, __global float *C, __global float *U,
                               int vertexCount, int sourceVertex )
{
    int tid = get_global_id(0);
    if (tid == sourceVertex)
    {
        M[tid] = 1;
        P[tid] = tid;
        C[tid] = 0.0;
        U[tid] = 0.0;
    }
    if (tid != sourceVertex)
    {
        C[tid] = FLT_MAX;;
        U[tid] = FLT_MAX;;
        P[tid] = -1;
    }
}




